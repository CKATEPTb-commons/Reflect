package dev.ckateptb.reflection;

import lombok.SneakyThrows;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Reflect<T> {
    private static final Map<Class<?>, ReflectClass<?>> classCache = new HashMap<>();
    protected final Class<T> clazz;
    protected final T object;

    @SuppressWarnings("unchecked")
    private Reflect(T object) {
        this((Class<T>) object.getClass(), object);
    }

    private Reflect(Class<T> clazz, T object) {
        this.clazz = clazz;
        this.object = object;
    }

    @SuppressWarnings("unchecked")
    public static <T> ReflectClass<T> classOf(Class<T> clazz) {
        return (ReflectClass<T>) classCache.computeIfAbsent(clazz, ReflectClass::new);
    }

    public static ReflectJar jarOf(Class<?> clazz) throws NullPointerException {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        return jarOf(new File(resource.getFile()));
    }

    public static ReflectJar jarOf(File file) {
        return new ReflectJar(file);
    }

    public static <S> Reflect<S> on(S obj) {
        return new Reflect<>(obj);
    }

    public static <S> Reflect<S> on(Class<S> clazz) {
        return on(clazz, null);
    }

    public static <S> Reflect<S> on(Class<S> clazz, S obj) {
        return new Reflect<>(clazz, obj);
    }

    public Class<T> type() {
        return this.clazz;
    }

    public T get() {
        return this.object;
    }

    public boolean isPresent() {
        return this.object != null;
    }

    public Reflect<T> ifPresent(Consumer<T> consumer) {
        if (this.object != null) {
            consumer.accept(this.object);
        }
        return this;
    }

    public Reflect<T> peek(Consumer<Reflect<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    public <P> Reflect<P> map(Function<Reflect<T>, Reflect<P>> mapper) {
        return mapper.apply(this);
    }

    public ConstructorReflect<T> constructor() {
        ReflectConstructor<T> constructor = classOf(this.clazz).getConstructor();
        constructor.get().setAccessible(true);
        return new ConstructorReflect<>(this.clazz, null, constructor);
    }

    public ConstructorReflect<T> constructor(Class<?>... parameters) {
        ReflectConstructor<T> constructor = classOf(this.clazz).getConstructorWithParams(parameters);
        constructor.get().setAccessible(true);
        return new ConstructorReflect<>(this.clazz, null, constructor);
    }

    public Collection<ConstructorReflect<T>> constructorAnnotated(Class<? extends Annotation> annotation) {
        return classOf(this.clazz).getConstructorsWithAnnotation(annotation).stream()
                .peek(constructor -> constructor.get().setAccessible(true))
                .map(constructor -> new ConstructorReflect<>(this.clazz, null, constructor))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<ConstructorReflect<T>> constructors() {
        return classOf(this.clazz).getConstructors().stream()
                .peek(constructor -> constructor.get().setAccessible(true))
                .map(constructor -> new ConstructorReflect<>(this.clazz, null, constructor))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<MethodReflect<?>> methods(String name) {
        return classOf(this.clazz).getMethodsByName(name).stream()
                .map(this::adapt)
                .collect(Collectors.toUnmodifiableSet());
    }

    public MethodReflect<?> method(String name) {
        return this.methods(name).stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <R> Collection<MethodReflect<R>> methods(Class<R> type, String name) {
        return classOf(this.clazz).getMethodsByName(name).stream()
                .filter(method -> method.getReturnType().equals(type))
                .map(this::adapt)
                .map(r -> (MethodReflect<R>) r)
                .collect(Collectors.toUnmodifiableSet());
    }

    public <R> MethodReflect<R> method(Class<R> type, String name) {
        return this.methods(type, name).stream().findFirst().orElse(null);
    }

    public <R> MethodReflect<R> method(String name, Class<?>... parameters) {
        return this.method(null, name, parameters);
    }

    public <R> MethodReflect<R> method(Class<R> type, String name, Class<?>... parameters) {
        return this.adapt(classOf(this.clazz).getMethodByNameAndParams(name, parameters));
    }

    public Collection<MethodReflect<?>> methodsAnnotated(Class<? extends Annotation> type) {
        return classOf(this.clazz).getMethodsWithAnnotation(type).stream()
                .map(this::adapt)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<MethodReflect<?>> methods(Class<?>... parameters) {
        return classOf(this.clazz).getMethodsWithParms(parameters).stream()
                .map(this::adapt)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public <R> Collection<MethodReflect<R>> methodsReturn(Class<R> type) {
        return classOf(this.clazz).getMethodsByReturnType(type).stream()
                .map(this::adapt)
                .map(r -> (MethodReflect<R>) r)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<MethodReflect<?>> methods() {
        return classOf(this.clazz).getMethods().stream()
                .map(this::adapt)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    private <R> MethodReflect<R> adapt(ReflectMethod reflect) {
        Method method = reflect.get();
        method.setAccessible(true);
        Class<R> type = (Class<R>) method.getReturnType();
        return new MethodReflect<>(type, null, reflect, this.object);
    }

    public <R> FieldReflect<R> field(String name) {
        return this.field(null, name);
    }

    public <R> FieldReflect<R> field(Class<R> type, String name) {
        return this.adapt(classOf(this.clazz).getFieldByName(name));
    }

    public Collection<FieldReflect<?>> fieldsAnnotated(Class<? extends Annotation> type) {
        return classOf(this.clazz).getFieldsWithAnnotation(type).stream()
                .map(this::adapt)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @SuppressWarnings("unchecked")
    public <R> Collection<FieldReflect<R>> fields(Class<R> type) {
        return classOf(this.clazz).getFieldsByType(type).stream()
                .map(this::adapt)
                .filter(Objects::nonNull)
                .map(r -> (FieldReflect<R>) r)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<FieldReflect<?>> fields() {
        return classOf(this.clazz).getFields().stream()
                .map(this::adapt)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public FieldReflect<T> asFields() {
        return (FieldReflect<T>) this;
    }

    public MethodReflect<T> asMethod() {
        return (MethodReflect<T>) this;
    }

    public ConstructorReflect<T> asConstructor() {
        return (ConstructorReflect<T>) this;
    }

    @SuppressWarnings("unchecked")
    private <R> FieldReflect<R> adapt(ReflectField reflect) {
        Field field = reflect.get();
        field.setAccessible(true);
        try {
            Class<R> type = (Class<R>) field.getType();
            R obj = (R) field.get(this.object);
            return new FieldReflect<>(type, obj, reflect, this.object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    protected Reflect<T> update(T value) {
        return new Reflect<>(this.clazz, value);
    }

    public <F> F as(Class<F> proxy, Class<?>... additionalInterfaces) {
        org.joor.Reflect joor;
        if (this.isPresent()) {
            joor = org.joor.Reflect.on(this.object);
        } else {
            joor = org.joor.Reflect.onClass(this.clazz);
        }
        return joor.as(proxy, additionalInterfaces);
    }

    public static class FieldReflect<T> extends Reflect<T> {
        private final ReflectField reference;
        private final Object referenceTarget;

        private FieldReflect(Class<T> clazz, T object, ReflectField reference, Object referenceTarget) {
            super(clazz, object);
            this.reference = reference;
            this.referenceTarget = referenceTarget;
        }

        @SneakyThrows
        public FieldReflect<T> set(T newValue) {
            if (this.reference == null)
                throw new NullPointerException("Failed to set a new value because the reference source is null.");
            this.reference.get().set(this.referenceTarget, newValue);
            return this.update(newValue);
        }

        public ReflectField getField() {
            return this.reference;
        }

        @Override
        protected FieldReflect<T> update(T value) {
            return new FieldReflect<>(this.clazz, value, this.reference, this.referenceTarget);
        }
    }

    public static class MethodReflect<T> extends Reflect<T> {
        private final ReflectMethod reference;
        private final Object referenceTarget;


        private MethodReflect(Class<T> clazz, T object, ReflectMethod reference, Object referenceTarget) {
            super(clazz, object);
            this.reference = reference;
            this.referenceTarget = referenceTarget;
        }

        @SneakyThrows
        @SuppressWarnings("unchecked")
        public Reflect<T> call(Object... args) {
            T invoke = (T) this.reference.get().invoke(referenceTarget, args);
            return this.update(invoke);
        }

        public ReflectMethod getMethod() {
            return this.reference;
        }

        @Override
        protected MethodReflect<T> update(T value) {
            return new MethodReflect<>(this.clazz, value, this.reference, this.referenceTarget);
        }
    }

    public static class ConstructorReflect<T> extends Reflect<T> {
        private final ReflectConstructor<T> reference;

        private ConstructorReflect(Class<T> clazz, T object, ReflectConstructor<T> reference) {
            super(clazz, object);
            this.reference = reference;
        }

        @SneakyThrows
        public Reflect<T> newInstance(Object... args) {
            T invoke = this.reference.get().newInstance(args);
            return this.update(invoke);
        }

        public ReflectConstructor<T> getConstructor() {
            return this.reference;
        }

        @Override
        protected ConstructorReflect<T> update(T value) {
            return new ConstructorReflect<>(this.clazz, value, this.reference);
        }
    }
}