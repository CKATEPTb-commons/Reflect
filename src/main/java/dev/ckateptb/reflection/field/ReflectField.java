package dev.ckateptb.reflection.field;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.api.AnnotationHolder;
import dev.ckateptb.reflection.api.ModifierHolder;
import dev.ckateptb.reflection.api.NameHolder;
import dev.ckateptb.reflection.api.ValueHolder;
import dev.ckateptb.reflection.atomic.AtomicOnce;
import dev.ckateptb.reflection.processor.FieldProcessor;
import dev.ckateptb.reflection.type.IReflectClass;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Concrete implementation of {@link IReflectField}, providing reflective
 * access to a field's metadata and value manipulation on a bound instance.
 * <p>
 * Delegates type introspection to an {@link IReflectClass} for the field's type,
 * and uses a cached {@link FieldProcessor} to perform get/set operations efficiently.
 * </p>
 *
 * @param <T> the field value type
 */
public class ReflectField<T> implements IReflectField<T> {
    /**
     * The underlying raw {@link Field} instance.
     */
    @Getter
    private final Field raw;

    /**
     * Reflective representation of the field's type.
     */
    @Delegate(excludes = {AnnotationHolder.class, ModifierHolder.class, NameHolder.class, ValueHolder.class})
    private final IReflectClass<T> type;

    /**
     * Lazy, thread-safe holder of the {@link FieldProcessor} instance.
     * <p>
     * The processor is computed at most once. The first caller executes the
     * supplier synchronously; concurrent callers wait and reuse the same instance.
     * </p>
     */
    private final AtomicOnce<FieldProcessor<T>> processor = new AtomicOnce<>();

    /**
     * Constructs a new ReflectField for the given raw field.
     *
     * @param raw the field to wrap
     */
    @SuppressWarnings("unchecked")
    public ReflectField(Field raw) {
        this.raw = raw;
        this.type = (IReflectClass<T>) Reflect.on(raw.getType());
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the field
     */
    @Override
    public String getName() {
        return this.raw.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @param <R> the type of the declaring class
     * @return an {@link IReflectClass} representing the declaring class
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getDeclaringClass() {
        return (IReflectClass<R>) Reflect.on(this.raw.getDeclaringClass());
    }

    /**
     * {@inheritDoc}
     *
     * @param updateFunction function that computes the new value from the current one
     * @return this field reflector for chaining
     */
    @Override
    public IReflectField<T> updateValue(Function<T, T> updateFunction) {
        T value = this.getValue();
        T apply = updateFunction.apply(value);
        return this.setValue(apply);
    }

    /**
     * Retrieves the current value of this field from the bound instance or class.
     *
     * @return the current field value
     */
    @Override
    public T getValue() {
        return this.getProcessor().get(this.isStatic() ? null : this.getDeclaringClass().getValue());
    }

    /**
     * Sets the value of this field on the bound instance or class.
     *
     * @param value the new value to assign
     * @return this field reflector for chaining
     */
    @Override
    public IReflectField<T> setValue(T value) {
        this.getProcessor().set(this.isStatic() ? null : this.getDeclaringClass().getValue(), value);
        return this;
    }

    /**
     * Returns the cached {@link FieldProcessor} for performing get/set operations.
     *
     * @return the FieldProcessor bound to the raw field
     */
    @SuppressWarnings("unchecked")
    public FieldProcessor<T> getProcessor() {
        return this.processor.getOrCompute(() -> {
            this.raw.trySetAccessible();
            try {
                VarHandle varHandle = MethodHandles.privateLookupIn(this.raw.getDeclaringClass(), MethodHandles.lookup())
                        .unreflectVarHandle(this.raw);
                return Modifier.isStatic(this.getModifiers()) ? new FieldProcessor<>(
                        (target) -> (T) varHandle.get(),
                        (target, args) -> varHandle.set(args)
                ) : new FieldProcessor<>(
                        (target) -> (T) varHandle.get(target),
                        varHandle::set
                );
            } catch (IllegalAccessException e) {
                final BiFunction<Object, Callable<T>, T> tryAccess = (target, callable) -> {
                    if (!this.raw.canAccess(target)) this.raw.setAccessible(true);
                    try {
                        return callable.call();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                };
                return new FieldProcessor<T>(
                        (target) -> tryAccess.apply(target, () -> (T) this.raw.get(target)),
                        (target, value) -> tryAccess.apply(target, () -> {
                            this.raw.set(target, value);
                            return null;
                        })
                );
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return this.raw.getModifiers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.raw.isAnnotationPresent(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.raw.getAnnotation(annotation);
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code ReflectField<R>}
     */
    public <R> ReflectField<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code ContextMethod<R>}
     */
    @SuppressWarnings("unchecked")
    public <R> ReflectField<R> cast() {
        return (ReflectField<R>) this;
    }
}
