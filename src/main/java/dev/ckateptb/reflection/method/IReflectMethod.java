package dev.ckateptb.reflection.method;

import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.parameter.ReflectParameter;
import dev.ckateptb.reflection.type.IReflectClass;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Reflective representation of a method, providing metadata access,
 * parameter inspection, invocation, and return-type handling.
 *
 * @param <T> the return type of the method when invoked
 */
public interface IReflectMethod<T> extends IReflectClass<T> {
    /**
     * Retrieves the underlying raw {@link Method} instance.
     *
     * @return the raw Method
     */
    Method getRaw();

    /**
     * Gets the name of the method as declared.
     *
     * @return the method name
     */
    String getName();

    /**
     * Invokes this method on a bound instance or statically if applicable,
     * with the provided arguments, returning a reflective handle to the result.
     *
     * @param args the arguments to pass to the method
     * @return an {@link IReflectClass} wrapping the method's return value
     */
    IReflectClass<T> invoke(Object... args);

    /**
     * Retrieves a reflective handle to the method's declared return type.
     *
     * @param <R> the return type
     * @return an {@link IReflectClass} representing the return type
     */
    <R> IReflectClass<R> getReturnType();

    /**
     * Retrieves all parameters of the method.
     *
     * @return a collection of {@link ReflectParameter} for each parameter
     */
    default Collection<ReflectParameter<?>> getParameters() {
        return this.getParameters(param -> true);
    }

    /**
     * Retrieves parameters matching the given filter.
     *
     * @param filter predicate to apply to each parameter
     * @return a collection of {@link ReflectParameter} instances satisfying the filter
     */
    Collection<ReflectParameter<?>> getParameters(Predicate<ReflectParameter<?>> filter);

    /**
     * {@inheritDoc}
     * <p>
     * Not supported for methods; always deprecated.
     * </p>
     *
     * @deprecated use {@link #invoke(Object...)} instead
     */
    @Override
    @Deprecated
    T getValue();

    /**
     * Retrieves a reflective handle to the declaring class of this method.
     *
     * @param <R> the type of the declaring class
     * @return an {@link IReflectClass} representing the declaring class
     */
    <R> IReflectClass<R> getDeclaringClass();
    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code IReflectMethod<R>}
     */
    default <R> IReflectMethod<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code IReflectMethod<R>}
     */
    @SuppressWarnings("unchecked")
    default <R> IReflectMethod<R> cast() {
        return (IReflectMethod<R>) this;
    }
}
