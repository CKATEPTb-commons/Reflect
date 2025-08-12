package dev.ckateptb.reflection.api;

import dev.ckateptb.reflection.type.IReflectClass;

/**
 * Interface for holding a value of type T.
 *
 * @param <T> the type of the value to hold
 */
public interface ValueHolder<T> {
    /**
     * Retrieves the current value held by this holder.
     *
     * @return the value stored in this holder
     */
    T getValue();

    /**
     * Sets a new value for this holder and returns an instance of IReflectClass representing the new value type.
     *
     * @param value the new value to set
     * @return an instance of IReflectClass that reflects the class of the new value
     */
    IReflectClass<T> setValue(T value);
}
