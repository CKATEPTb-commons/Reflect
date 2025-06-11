package dev.ckateptb.reflection;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReflectJar implements AutoCloseable {
    private final JarFile jar;

    public Collection<String> paths() {
        return this.paths(path -> true);
    }

    public Collection<String> paths(Predicate<String> filter) {
        Set<String> paths = new HashSet<>();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                String path = entry.getName().replace('/', '.').replace(".class", "");
                if (filter.test(path)) paths.add(path);
            }
        }
        return Collections.unmodifiableSet(paths);
    }

    public Collection<ReflectClass<?>> classes() {
        return this.classes(path -> true, clazz -> true);
    }

    public Collection<ReflectClass<?>> classes(Predicate<ReflectClass<?>> filter) {
        return this.classes(path -> true, filter);
    }

    public Collection<ReflectClass<?>> classes(Predicate<String> pathFilter, Predicate<ReflectClass<?>> filter) {
        return this.paths(pathFilter).stream().map(Reflect::on).filter(filter).collect(Collectors.toList());
    }

    @Override
    public void close() throws Exception {
        this.jar.close();
    }
}
