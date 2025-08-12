package dev.ckateptb.reflection;

import dev.ckateptb.reflection.file.ReflectFile;
import dev.ckateptb.reflection.type.IReflectClass;
import dev.ckateptb.reflection.type.ReflectClass;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.jar.JarFile;

/**
 * Utility entry point for reflection operations.
 * <p>
 * Provides methods to obtain reflective handles {@link IReflectClass})
 * for classes or instances, backed by an internal cache for performance.
 * </p>
 */
public class Reflect {
    // noinspection FieldMayBeFinal
    /**
     * The {@link ClassLoader} used for all reflection operations.
     * <p>
     * By default, this is initialized to the current thread's context ClassLoader.
     * If that is {@code null}, it falls back to the ClassLoader that loaded
     * {@code Reflect} itself.
     * </p>
     * <p>
     * If necessary, you can change this ClassLoader at runtime via reflection.
     * </p>
     */
    @Setter
    @Getter
    private static ClassLoader classLoader = Optional.ofNullable(
            Thread.currentThread().getContextClassLoader()
    ).orElse(Reflect.class.getClassLoader());

    /**
     * Cache of reflective class wrappers, keyed by the target class.
     */
    private static final ClassValue<ReflectClass<?>> classes = new ClassValue<>() {
        @Override
        protected ReflectClass<?> computeValue(Class<?> type) {
            return new ReflectClass<>(type);
        }
    };

    public static ReflectFile scan(JarFile jar) {
        return new ReflectFile(jar);
    }

    /**
     * Loads the class with the given fully qualified name and returns its reflective wrapper.
     * <p>
     * Uses the current thread context class loader if available, otherwise falls back
     * to the class loader that loaded {@code Reflect}.
     * </p>
     *
     * @param clazz the fully qualified name of the class to reflect
     * @return a {@link IReflectClass} for the loaded class
     */
    @SneakyThrows
    public static IReflectClass<?> on(String clazz) {
        return Reflect.on(Class.forName(clazz, false, classLoader));
    }

    /**
     * Returns a cached or new reflective wrapper for the given class.
     *
     * @param <T>   the type represented by the class
     * @param clazz the {@link Class} object to reflect
     * @return a {@link IReflectClass} instance for the specified class
     */
    @SuppressWarnings("unchecked")
    public static <T> IReflectClass<T> on(Class<T> clazz) {
        return (ReflectClass<T>) classes.get(clazz);
    }

    /**
     * Returns a reflective wrapper for the given object instance.
     * <p>
     * If the object is {@code null}, this method returns {@code null}.
     * </p>
     *
     * @param <T>    the type of the object instance
     * @param object the instance to reflect
     * @return an {@link IReflectClass} bound to the provided instance, or {@code null} if
     * the input is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> IReflectClass<T> on(T object) {
        if (object == null) return null;
        return Reflect.on((Class<T>) object.getClass()).setValue(object);
    }
}