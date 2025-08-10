package dev.ckateptb.reflection.file;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.api.AnnotationHolder;
import dev.ckateptb.reflection.api.ModifierHolder;
import dev.ckateptb.reflection.api.NameHolder;
import dev.ckateptb.reflection.type.IReflectClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.jar.JarEntry;

@RequiredArgsConstructor
public class ReflectUnloadedClass implements NameHolder, AnnotationHolder, ModifierHolder {
    @Getter
    private final ReflectFile jar;
    private final JarEntry entry;
    private final AtomicReference<IReflectClass<?>> cache = new AtomicReference<>();

    @Override
    public int getModifiers() {
        return this.load().getModifiers();
    }

    public IReflectClass<?> load() {
        return Reflect.on(this.getName());
    }

    public ReflectUnloadedClass peek(Consumer<ReflectUnloadedClass> consumer) {
        consumer.accept(this);
        return this;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.load().is
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return null;
    }

    @Override
    public String getName() {
        // todo
    }

    public String getSimpleName() {
        // todo
    }

    public String getPackage() {
        // todo
    }

    public boolean isInstanceOf(Class<?> clazz) {
        // todo
    }
}