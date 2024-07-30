package dev.ckateptb.reflection;

import lombok.experimental.Delegate;

import java.lang.reflect.Constructor;

public class ReflectConstructor<T> extends ReflectWrapper<Constructor<T>> {
    @Delegate
    private final Constructor<T> constructor;

    ReflectConstructor(Constructor<T> constructor) {
        super(constructor);
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    @Override
    public int modifiers() {
        return this.target.getModifiers();
    }
}