package dev.ckateptb.reflection.processor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Processor for reflective access to a specific {@link Field}, providing
 * cached getter and setter logic using {@link VarHandle} when available,
 * or falling back to {@link Field#get(Object)} and {@link Field#set(Object, Object)}.
 * <p>
 * Instances are cached per {@link Field} to avoid repeated lookup and accessibility checks.
 * </p>
 *
 * @param <T> the field value type
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldProcessor<T> implements Processor {
    /**
     * Cache of FieldProcessor instances keyed by Field.
     */
    private static final Cache<Field, FieldProcessor<?>> PROCESSORS = Caffeine.newBuilder().build();
    /**
     * Function to get the field value from a target instance.
     */
    private final Function<Object, T> getter;
    /**
     * Consumer to set the field value on a target instance.
     */
    private final BiConsumer<Object, T> setter;

    /**
     * Creates or retrieves a cached {@link FieldProcessor} for the given field.
     * <p>
     * Attempts to obtain a {@link VarHandle} for improved access performance,
     * falling back to conventional reflective {@link Field} access if needed.
     * </p>
     *
     * @param field the {@link Field} to process
     * @param <T>   the expected field value type
     * @return a cached FieldProcessor configured for the given field
     */
    @SuppressWarnings("unchecked")
    static <T> FieldProcessor<T> from(Field field) {
        return (FieldProcessor<T>) PROCESSORS.get(field, key -> {
            field.trySetAccessible();
            try {
                VarHandle varHandle = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup())
                        .unreflectVarHandle(field);
                return Modifier.isStatic(field.getModifiers()) ? new FieldProcessor<>(
                        (target) -> (T) varHandle.get(),
                        (target, args) -> varHandle.set(args)
                ) : new FieldProcessor<>(
                        (target) -> (T) varHandle.get(target),
                        varHandle::set
                );
            } catch (IllegalAccessException e) {
                final BiFunction<Object, Callable<T>, T> tryAccess = (target, callable) -> {
                    if (!field.canAccess(target)) field.setAccessible(true);
                    try {
                        return callable.call();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                };
                return new FieldProcessor<T>(
                        (target) -> tryAccess.apply(target, () -> (T) field.get(target)),
                        (target, value) -> tryAccess.apply(target, () -> {
                            field.set(target, value);
                            return null;
                        })
                );
            }
        });
    }

    /**
     * Retrieves the field value from the given target instance.
     *
     * @param target the instance from which to get the field value
     * @return the field value
     */
    public T get(Object target) {
        return this.getter.apply(target);
    }

    /**
     * Sets the field value on the given target instance.
     *
     * @param target the instance on which to set the field value
     * @param value  the value to set
     */
    public void set(Object target, T value) {
        this.setter.accept(target, value);
    }
}
