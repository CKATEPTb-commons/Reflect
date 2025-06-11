package dev.ckateptb.reflection;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.util.jar.JarFile;


@RequiredArgsConstructor
public class Reflect<T> {
    protected final Reflect<?> host;
    protected final Class<T> type;

    @SneakyThrows
    public static ReflectClass<?> on(String string) {
        return Reflect.on(Class.forName(string));
    }

    @SuppressWarnings("unchecked")
    public static <T> ReflectClass<T> on(T object) {
        return new ReflectClass<>(null, (Class<T>) object.getClass(), object);
    }

    public static <T> ReflectClass<T> on(Class<T> clazz) {
        return new ReflectClass<>(null, clazz, null);
    }

    public static <T> ReflectJar on(JarFile jar) {
        return new ReflectJar(jar);
    }

    @SneakyThrows
    public static <T> ReflectJar on(File file) {
        return on(new JarFile(file));
    }

    public Class<T> raw() {
        return this.type;
    }

    public ReflectClass<T> type() {
        return Reflect.on(this.type);
    }
}