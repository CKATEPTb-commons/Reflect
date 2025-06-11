package dev.ckateptb.reflection;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ReflectConstructor<T> extends Reflect<T> implements AnnotationHolder, ModifierHolder, NameHolder {
    private final Constructor<T> constructor;

    public ReflectConstructor(Reflect<T> host, Class<T> type, Constructor<T> constructor) {
        super(host, type);
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    @SneakyThrows
    public ReflectClass<T> invoke(Object... args) {
        return new ReflectClass<>(this.host.host, this.type, this.constructor.newInstance(args));
    }

    public Parameter[] parameters() {
        return this.constructor.getParameters();
    }

    @Override
    public String name() {
        return this.constructor.getName();
    }

    @Override
    public int modifiers() {
        return this.constructor.getModifiers();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.constructor.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> A annotation(Class<A> annotation) {
        return this.constructor.getAnnotation(annotation);
    }

}
