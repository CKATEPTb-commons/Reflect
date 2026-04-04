package dev.ckateptb.reflection.constructor;

import dev.ckateptb.reflection.parameter.ReflectParameter;
import dev.ckateptb.reflection.type.IReflectClass;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Reflective representation of a constructor, providing metadata access,
 * parameter inspection, invocation, and return-type handling for creating new instances.
 *
 * @param <T> the type constructed by this constructor
 */
public interface IReflectConstructor<T> extends IReflectClass<T> {
    /**
     * Retrieves the underlying raw {@link Constructor} instance.
     *
     * @return the raw Constructor
     */
    Constructor<T> getRaw();

    /**
     * Invokes this constructor with the provided arguments, returning a reflective handle to the new instance.
     *
     * @param args the arguments to pass to the constructor
     * @return an {@link IReflectClass} wrapping the newly created instance
     */
    IReflectClass<T> invoke(Object... args);

    /**
     * Retrieves a reflective handle to the constructed type (often same as {@link #getType()}).
     *
     * @param <R> the constructed type
     * @return an {@link IReflectClass} representing the constructed type
     */
    <R> IReflectClass<R> getReturnType();

    /**
     * Retrieves all parameters of this constructor.
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
     * Finds the first parameter matching the given filter.
     *
     * @param filter predicate to apply to each parameter
     * @return an Optional containing the first {@link ReflectParameter} satisfying the filter
     */
    default Optional<ReflectParameter<?>> findFirstParameter(Predicate<ReflectParameter<?>> filter) {
        return this.getParameters(filter).stream().findFirst();
    }

    /**
     * Retrieves the first parameter matching the given filter.
     *
     * @param filter predicate to apply to each parameter
     * @return the first {@link ReflectParameter} satisfying the filter
     * @throws java.util.NoSuchElementException if no parameter matches
     */
    default ReflectParameter<?> getFirstParameter(Predicate<ReflectParameter<?>> filter) {
        return this.findFirstParameter(filter).orElseThrow();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Not supported for constructors; always deprecated.
     * </p>
     *
     * @deprecated use {@link #invoke(Object...)} instead
     */
    @Override
    @Deprecated
    T getValue();

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code IReflectConstructor<R>}
     */
    default <R> IReflectConstructor<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code IReflectConstructor<R>}
     */
    @SuppressWarnings("unchecked")
    default <R> IReflectConstructor<R> cast() {
        return (IReflectConstructor<R>) this;
    }
}
