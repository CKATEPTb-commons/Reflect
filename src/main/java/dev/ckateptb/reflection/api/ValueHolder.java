package dev.ckateptb.reflection.api;

import dev.ckateptb.reflection.type.IReflectClass;

public interface ValueHolder<T> {
    T getValue();

    IReflectClass<T> setValue(T value);
}
