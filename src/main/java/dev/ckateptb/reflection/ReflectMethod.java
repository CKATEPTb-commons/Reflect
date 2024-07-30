package dev.ckateptb.reflection;

import lombok.experimental.Delegate;

import java.lang.reflect.Method;

public class ReflectMethod extends ReflectWrapper<Method> {
    @Delegate
    private final Method method;

    ReflectMethod(Method method) {
        super(method);
        this.method = method;
        this.method.setAccessible(true);
    }

    @Override
    public int modifiers() {
        return this.getModifiers();
    }
}
