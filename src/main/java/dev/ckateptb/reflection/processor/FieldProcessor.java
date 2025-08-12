package dev.ckateptb.reflection.processor;

import lombok.RequiredArgsConstructor;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
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
@RequiredArgsConstructor
public class FieldProcessor<T> {
    /**
     * Function to get the field value from a target instance.
     */
    private final Function<Object, T> getter;
    /**
     * Consumer to set the field value on a target instance.
     */
    private final BiConsumer<Object, T> setter;

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
