package dev.ckateptb.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectClass<T> extends Reflect<T> implements ModifierHolder, AnnotationHolder, NameHolder, ValueHolder<T> {
    private final T instance;

    public ReflectClass(Reflect<?> host, Class<T> clazz, T instance) {
        super(host, clazz);
        this.instance = instance;
    }

    public Collection<ReflectField<?>> fields() {
        HashSet<ReflectField<?>> fields = new HashSet<>();
        Class<? super T> superclass = this.type.getSuperclass();
        if (superclass != null) {
            fields.addAll(Reflect.on(superclass).fields());
        }
        fields.addAll(Reflect.on(this.type).fields());
        Field[] declaredFields = this.type.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object instance = null;
            try {
                instance = field.get(this.instance);
            } catch (Throwable ignored) {
            }
            fields.add(new ReflectField<>(this, field, instance));
        }
        return Collections.unmodifiableSet(fields);
    }

    public ReflectField<?> field(String name) {
        return this.fields().stream()
                .filter(field -> field.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Field with name %s not found!", name)));
    }

    @SuppressWarnings("unchecked")
    public <R> Collection<ReflectField<R>> fields(Class<R> type) {
        return this.fields().stream()
                .filter(field -> field.raw().equals(type))
                .map(field -> (ReflectField<R>) field)
                .toList();
    }

    public <R> ReflectField<R> field(Class<R> type) {
        return this.fields(type).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Field with type %s not found!", type)));
    }

    public Collection<ReflectField<?>> fields(Predicate<ReflectField<?>> filter) {
        return this.fields().stream().filter(filter).toList();
    }

    public ReflectField<?> field(Predicate<ReflectField<?>> filter) {
        return this.fields(filter).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Field that match predicate not found!"));
    }

    @SuppressWarnings("unchecked")
    public Collection<ReflectConstructor<T>> constructors() {
        Set<ReflectConstructor<T>> constructors = new HashSet<>();
        for (Constructor<T> constructor : (Constructor<T>[]) this.type.getConstructors()) {
            constructors.add(new ReflectConstructor<>(this, this.type, constructor));
        }
        return Collections.unmodifiableSet(constructors);
    }

    public ReflectConstructor<T> constructor() {
        return this.constructors().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Constructor for " + type.getName() + " not found!"));
    }

    public ReflectConstructor<T> constructor(Parameter... parameters) {
        return this.constructors(constructor -> Arrays.equals(constructor.parameters(), parameters))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Constructor for " + type.getName() +
                        " with specified parameters " + Arrays.toString(parameters) + " not found!"));
    }

    public ReflectConstructor<T> constructor(Class<?>... parameters) {
        return this.constructors(constructor -> Arrays.equals(Arrays.stream(constructor.parameters())
                        .map(Parameter::getType).toArray(), parameters))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Constructor for " + type.getName() +
                        " with specified parameters " + Arrays.toString(parameters) + " not found!"));
    }

    public Collection<ReflectConstructor<T>> constructors(String... parameters) {
        return this.constructors(constructor -> Arrays.equals(Arrays.stream(constructor.parameters())
                .map(Parameter::getName).toArray(), parameters));
    }

    public ReflectConstructor<T> constructor(String... parameters) {
        return this.constructors(parameters)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Constructor for " + type.getName() +
                        " with specified parameters " + Arrays.toString(parameters) + " not found!"));
    }

    public Collection<ReflectConstructor<T>> constructors(Predicate<ReflectConstructor<T>> filter) {
        return this.constructors().stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableSet());
    }

    public ReflectConstructor<T> constructor(Predicate<ReflectConstructor<T>> filter) {
        return this.constructors(filter)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Constructor for " + type.getName() +
                        " with specified predicate not found!"));
    }

    public Collection<ReflectMethod<?>> methods() {
        return this.methods(this);
    }

    public Collection<ReflectMethod<?>> methods(String name) {
        return this.methods().stream()
                .filter(m -> m.name().equals(name)).collect(Collectors.toUnmodifiableSet());
    }

    public ReflectMethod<?> method(String name) {
        return this.methods(name).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method with specified name " + name + " not found!"));
    }

    private Collection<ReflectMethod<?>> methods(ReflectClass<?> reflect) {
        Set<ReflectMethod<?>> methods = new HashSet<>();
        for (Class<?> clazz : this.type.getInterfaces()) {
            methods.addAll(Reflect.on(clazz).methods(reflect));
        }
        Class<? super T> superclass = this.type.getSuperclass();
        if (superclass != null) {
            methods.addAll(Reflect.on(superclass).methods(reflect));
        }
        methods.addAll(Arrays.stream(this.type.getDeclaredMethods())
                .map(method -> new ReflectMethod<>(reflect, method))
                .collect(Collectors.toSet()));
        return Collections.unmodifiableSet(methods);
    }

    public ReflectMethod<?> method(Predicate<ReflectMethod<?>> filter) {
        return this.methods(filter).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method with specified predicate not found!"));
    }

    public Collection<ReflectMethod<?>> methods(Predicate<ReflectMethod<?>> filter) {
        return this.methods().stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<ReflectMethod<?>> methods(Parameter... parameters) {
        return this.methods(method -> Arrays.equals(method.parameters(), parameters));
    }

    public Collection<ReflectMethod<?>> methods(Class<?>... parameters) {
        return this.methods(method -> Arrays.equals(Arrays.stream(method.parameters())
                .map(Parameter::getType).toArray(), parameters));
    }

    public Collection<ReflectMethod<?>> methods(String... parameters) {
        return this.methods(method -> Arrays.equals(Arrays.stream(method.parameters())
                .map(Parameter::getName).toArray(), parameters));
    }

    public ReflectMethod<?> method(String name, Class<?>... parameters) {
        return this.methods(method -> method.name().equals(name) && (parameters.length == 0 || Arrays.equals(Arrays.stream(method.parameters())
                        .map(Parameter::getType).toArray(), parameters)))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method with specified name" + name + " and parameters" + Arrays.toString(parameters) + "not found!"));
    }

    public ReflectMethod<?> method(String name, Parameter... parameters) {
        return this.methods(method -> method.name().equals(name) && (parameters.length == 0 || Arrays.equals(method.parameters(), parameters)))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method with specified name" + name + " and parameters" + Arrays.toString(parameters) + "not found!"));
    }

    public ReflectMethod<?> method(String name, String... parameters) {
        return this.methods(method -> method.name().equals(name) && (parameters.length == 0 || Arrays.equals(Arrays.stream(method.parameters())
                        .map(Parameter::getName).toArray(), parameters)))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method with specified name" + name + " and parameters" + Arrays.toString(parameters) + "not found!"));
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.type.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> A annotation(Class<A> annotation) {
        return this.type.getAnnotation(annotation);
    }

    @Override
    public int modifiers() {
        return this.type.getModifiers();
    }

    @Override
    public String name() {
        return this.type.getName();
    }

    @Override
    public T value() {
        return this.instance;
    }
}
