package dev.ckateptb.reflection.file;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.flag.Flag;
import dev.ckateptb.reflection.flag.FlagTracker;
import dev.ckateptb.reflection.type.ReflectClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReflectFile extends FlagTracker {
    private final static String versions = "META-INF/versions/";
    private final static int versionsLength = versions.length();
    @Getter
    protected final JarFile jar;
    private final Map<String, ReflectClass<?>> classes = new HashMap<>();

    public Collection<ReflectUnloadedClass> getClasses() {
        return this.getClassesByFilter(ignored -> true);
    }

    public Collection<ReflectUnloadedClass> getClassesInPackage(String pkg) {
        return this.getClassesInPackage(pkg, true);
    }

    public Collection<ReflectUnloadedClass> getClassesInPackage(String pkg, boolean recursive) {
        return this.getClassesByFilter(clazz -> {
            String clazzPackage = clazz.getPackage();
            clazzPackage = clazzPackage.isEmpty() ? clazzPackage : clazzPackage + ".";
            return recursive ? clazzPackage.startsWith(pkg) : clazzPackage.equals(pkg);
        });
    }

    @SneakyThrows
    public Collection<ReflectUnloadedClass> getClassesByFilter(Predicate<ReflectUnloadedClass> filter) {
        if (!this.hasFlag(Flag.CLASSES_CACHED)) {
            synchronized (this.classes) {
                if (!this.hasFlag(Flag.CLASSES_CACHED)) {
                    final boolean multiRelease = Optional.ofNullable(this.jar.getManifest())
                            .map(m -> "true".equalsIgnoreCase(m.getMainAttributes().getValue("Multi-Release")))
                            .orElse(false);
                    final int runtimeVersion = Runtime.version().feature();
                    Map<String, Map.Entry<Integer, JarEntry>> entries = new HashMap<>();
                    for (Enumeration<JarEntry> enumeration = this.jar.entries(); enumeration.hasMoreElements(); ) {
                        JarEntry entry = enumeration.nextElement();
                        String name = entry.getName();
                        int version = 0;
                        if (!name.endsWith(".class") || name.endsWith("/package-info.class") || name.equals("module-info.class"))
                            continue;
                        if (name.startsWith("META-INF/versions/")) {
                            if (!multiRelease) continue;
                            int slash = name.indexOf('/', versionsLength);
                            try {
                                version = Integer.parseInt(name.substring(versionsLength, slash));
                            } catch (NumberFormatException exception) {
                                continue;
                            }
                            if (version < 9 || version > runtimeVersion) continue;
                            name = name.substring(slash + 1);
                        } else if (name.startsWith("META-INF/")) continue;
                        int finalVersion = version;
                        entries.compute(name, (key, current) ->
                                current == null || finalVersion > current.getKey() ? Map.entry(finalVersion, entry) : current);
                    }
                    entries.forEach((key, value) -> {
                                this.classes.put(
                                        key.substring(0, key.length() - 6).replace('/', '.'),
                                        Reflect.on(value.getValue().getName())
                                        new ReflectUnloadedClass(this, value.getValue()));
                            }
                    );
                    this.addFlag(Flag.CLASSES_CACHED);
                }
            }
        }
        return this.classes.values().stream().filter(filter).collect(Collectors.toUnmodifiableSet());
    }

    public Collection<ReflectUnloadedClass> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getClassesByFilter(clazz -> clazz.isAnnotationPresent(annotation));
    }

    public Collection<ReflectUnloadedClass> getClassesByInstanceOf(Class<?> superClass) {
        return this.getClassesByFilter(clazz -> clazz.isInstanceOf(superClass));
    }
}