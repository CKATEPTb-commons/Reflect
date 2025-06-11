package dev.ckateptb.reflection;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectField<T> extends ReflectClass<T> {
    protected final Field field;

    @SuppressWarnings("unchecked")
    public ReflectField(ReflectClass<?> host, Field field, T instance) {
        super(host, (Class<T>) field.getType(), instance);
        this.field = field;
        this.field.setAccessible(true);
    }

    @SneakyThrows
    public ReflectField<T> set(T value) {
        this.field.set(((ReflectClass<?>) this.host).value(), value);
        return new ReflectField<>(((ReflectClass<?>) this.host), this.field, value);
    }

    @Override
    public String name() {
        return this.field.getName();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.field.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> A annotation(Class<A> annotation) {
        return this.field.getAnnotation(annotation);
    }

    @Override
    public int modifiers() {
        return this.field.getModifiers();
    }
}
