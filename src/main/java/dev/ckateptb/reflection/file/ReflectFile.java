package dev.ckateptb.reflection.file;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.flag.Flag;
import dev.ckateptb.reflection.flag.FlagTracker;
import dev.ckateptb.reflection.type.IReflectClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Represents a wrapper around a {@link JarFile} with reflection utilities
 * for scanning and loading classes from it.
 * <p>
 * This class supports multi-release JARs transparently, relying on the JVM's
 * built-in {@link URLClassLoader} behavior: if the JAR manifest contains
 * {@code Multi-Release: true}, the correct version of the class for the
 * current runtime will be loaded.
 * </p>
 * <p>
 * Classes are loaded in a lazy manner and cached for subsequent calls. They
 * are <b>loaded but not initialized</b> (i.e., {@code <clinit>} is not run)
 * until used, via {@link Class#forName(String, boolean, ClassLoader)} with
 * {@code initialize = false}.
 * </p>
 * <p>
 * Once the {@link URLClassLoader} is closed, all loaded classes remain usable
 * if fully resolved; however, any further attempts to load additional classes
 * or resources from the JAR will fail.
 * </p>
 */
@RequiredArgsConstructor
public class ReflectFile extends FlagTracker {
    /**
     * The underlying JAR file from which classes and resources are scanned.
     * <p>
     * This reference is used for enumerating entries and does not imply
     * that the JAR will remain open indefinitely; it should be closed
     * elsewhere if resource management is a concern.
     * </p>
     */
    @Getter
    protected final JarFile jar;
    /**
     * Cache of classes discovered in the {@link #jar}, keyed by their
     * fully qualified binary name (e.g., {@code com.example.MyClass}).
     * <p>
     * Each value is an {@link IReflectClass} wrapper around the loaded
     * {@link Class} instance. Classes are loaded but not initialized
     * until first use.
     * </p>
     */
    private final Map<String, IReflectClass<?>> classes = new HashMap<>();

    /**
     * Retrieves all classes from the JAR file.
     *
     * @return A collection of all classes in the JAR file.
     */
    public Collection<IReflectClass<?>> getClasses() {
        return this.getClassesByFilter(ignored -> true);
    }

    /**
     * Retrieves classes within a specific package.
     *
     * @param pkg The package name to filter by.
     * @return A collection of classes in the specified package.
     */
    public Collection<IReflectClass<?>> getClassesInPackage(String pkg) {
        return this.getClassesInPackage(pkg, true);
    }

    /**
     * Retrieves classes within a specific package, optionally recursively.
     *
     * @param pkg The package name to filter by.
     * @param recursive Whether to include sub-packages.
     * @return A collection of classes in the specified package and its sub-packages if recursive is true.
     */
    public Collection<IReflectClass<?>> getClassesInPackage(String pkg, boolean recursive) {
        return this.getClassesByFilter(clazz -> {
            String clazzPackage = clazz.getPackage();
            clazzPackage = clazzPackage.isEmpty() ? clazzPackage : clazzPackage + ".";
            return recursive ? clazzPackage.startsWith(pkg) : clazzPackage.equals(pkg);
        });
    }

    /**
     * Retrieves classes that match a given filter.
     *
     * @param filter The predicate to apply for filtering classes.
     * @return A collection of classes that match the filter.
     */
    @SneakyThrows
    public Collection<IReflectClass<?>> getClassesByFilter(Predicate<IReflectClass<?>> filter) {
        if (!this.hasFlag(Flag.CLASSES_CACHED)) {
            synchronized (this.classes) {
                if (!this.hasFlag(Flag.CLASSES_CACHED)) {
                    File file = new File(this.jar.getName());
                    URL url = file.toPath().toUri().toURL();
                    try (URLClassLoader loader = new URLClassLoader(new URL[]{url}, Reflect.getClassLoader())) {
                        for (Enumeration<JarEntry> enumeration = this.jar.entries(); enumeration.hasMoreElements(); ) {
                            JarEntry entry = enumeration.nextElement();
                            String name = entry.getName();
                            if (!name.endsWith(".class") || name.endsWith("/package-info.class") ||
                                    name.equals("module-info.class") || name.startsWith("META-INF"))
                                continue;
                            String clazz = name.substring(0, name.length() - 6).replace('/', '.');
                            try {
                                this.classes.put(clazz, Reflect.on(Class.forName(clazz, false, loader)));
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    this.addFlag(Flag.CLASSES_CACHED);
                }
            }
        }
        return this.classes.values().stream().filter(filter).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieves classes annotated with a specific annotation.
     *
     * @param annotation The annotation class to filter by.
     * @return A collection of classes that are annotated with the specified annotation.
     */
    public Collection<IReflectClass<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getClassesByFilter(clazz -> clazz.isAnnotationPresent(annotation));
    }

    /**
     * Retrieves classes that are instances of a specific superclass or interface.
     *
     * @param superClass The superclass or interface to filter by.
     * @return A collection of classes that are instances of the specified superclass or interface.
     */
    public Collection<IReflectClass<?>> getClassesByInstanceOf(Class<?> superClass) {
        return this.getClassesByFilter(clazz -> clazz.isInstanceOf(superClass));
    }
}