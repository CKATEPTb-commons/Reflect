package dev.ckateptb.reflection;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ReflectJar extends ReflectWrapper<File> {
    private final Set<String> classNamesCache = new HashSet<>();
    private final Map<String, Class<?>> classesCache = new HashMap<>();

    protected ReflectJar(File target) {
        super(target);
    }

    public Set<String> getClassNamesCache() throws IOException {
        if (this.classNamesCache.isEmpty()) {
            try (JarFile jar = new JarFile(this.target)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        this.classNamesCache.add(entry.getName()
                                .replace('/', '.')
                                .replace(".class", ""));
                    }
                }
            }
        }
        return Collections.unmodifiableSet(this.classNamesCache);
    }

    public Set<Reflect<?>> getClasses(ClassLoader classLoader, Predicate<String> filter) throws IOException {
        return this.getClassNamesCache().stream()
                .filter(filter)
                .map(clazz -> {
                    Class<?> cached = this.classesCache.get(clazz);
                    if (cached != null) return cached;
                    try {
                        Class<?> newClass = classLoader.loadClass(clazz);
                        this.classesCache.put(clazz, newClass);
                        return newClass;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Reflect::on)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public Set<Reflect<?>> getClassesSneaky(ClassLoader classLoader, Predicate<String> filter) {
        return this.getClasses(classLoader, filter);
    }

    @Override
    public int modifiers() {
        throw new UnsupportedOperationException("Not supported!");
    }
}
