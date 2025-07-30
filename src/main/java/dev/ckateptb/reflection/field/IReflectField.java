package dev.ckateptb.reflection.field;

import dev.ckateptb.reflection.constructor.IReflectConstructor;
import dev.ckateptb.reflection.type.IReflectClass;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * Reflective representation of a field, providing access to its metadata, raw {@link Field},
 * and operations to get or update its value on a bound instance.
 *
 * @param <T> the field value type
 */
public interface IReflectField<T> extends IReflectClass<T> {
    /**
     * Returns the underlying {@link Field} instance for this reflective field.
     *
     * @return the raw Field object
     */
    Field getRaw();

    /**
     * {@inheritDoc}
     * <p>
     * Binds a new value to this field on the reflective handle's instance.
     * </p>
     *
     * @param value the new value to set
     * @return this field reflector for chaining
     */
    @Override
    IReflectField<T> setValue(T value);

    /**
     * Updates the field's value on the bound instance using the provided function.
     *
     * @param updateFunction function that takes the current value and returns the new value
     * @return this field reflector for chaining
     */
    IReflectField<T> updateValue(Function<T, T> updateFunction);

    /**
     * Retrieves the declaring class of this field as a reflective handle.
     *
     * @param <R> the type of the declaring class
     * @return an {@link IReflectClass} representing the class that declares this field
     */
    <R> IReflectClass<R> getDeclaringClass();

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code IReflectField<R>}
     */
    default <R> IReflectField<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code IReflectField<R>}
     */
    @SuppressWarnings("unchecked")
    default <R> IReflectField<R> cast() {
        return (IReflectField<R>) this;
    }
}
