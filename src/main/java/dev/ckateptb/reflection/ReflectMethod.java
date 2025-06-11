package dev.ckateptb.reflection;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectMethod<T> extends Reflect<T> implements ModifierHolder, AnnotationHolder, NameHolder {
    private final Method method;

    @SuppressWarnings("unchecked")
    public ReflectMethod(ReflectClass<?> host, Method method) {
        super(host, (Class<T>) method.getReturnType());
        this.method = method;
        this.method.setAccessible(true);
    }

    @Override
    public String name() {
        return this.method.getName();
    }

    @Override
    public int modifiers() {
        return this.method.getModifiers();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.method.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> A annotation(Class<A> annotation) {
        return this.method.getAnnotation(annotation);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public ReflectClass<T> invoke(Object... args) {
        return new ReflectClass<>(this.host.host, this.type, (T) this.method.invoke(((ReflectClass<?>) this.host).value(), args));
    }

    public Parameter[] parameters() {
        return this.method.getParameters();
    }

}
