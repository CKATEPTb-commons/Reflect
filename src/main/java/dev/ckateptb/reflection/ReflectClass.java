package dev.ckateptb.reflection;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectClass<T> extends ReflectWrapper<Class<T>> {
    private final Map<String, ReflectField> fields = new HashMap<>();
    private final Map<String, ReflectConstructor<T>> constructors = new HashMap<>();
    private final Map<String, ReflectMethod> methods = new HashMap<>();

    ReflectClass(Class<T> clazz) {
        super(clazz);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.target.isAnnotationPresent(annotation);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.target.getAnnotation(annotation);
    }

    private Map<String, ReflectMethod> methods() {
        if (this.methods.isEmpty()) {
            Arrays.stream(this.target.getMethods()).map(ReflectMethod::new)
                    .forEach(reflectField -> this.methods.put(reflectField.getName(), reflectField));
            Arrays.stream(this.target.getDeclaredMethods()).map(ReflectMethod::new)
                    .forEach(reflectField -> this.methods.put(reflectField.getName(), reflectField));
        }
        return this.methods;
    }

    public Collection<ReflectMethod> getMethodsByName(String name) {
        return this.methods().values().stream()
                .filter(method -> method.get().getName().equals(name))
                .collect(Collectors.toList());
    }

    public Collection<ReflectMethod> getMethodsByReturnType(Class<?> returnType) {
        return this.methods().values().stream()
                .filter(method -> method.get().getReturnType().equals(returnType))
                .collect(Collectors.toList());
    }

    public Collection<ReflectMethod> getMethodsWithParms(Class<?>... parameterTypes) {
        return this.methods().values().stream()
                .filter(method -> Arrays.equals(method.get().getParameterTypes(), parameterTypes))
                .collect(Collectors.toList());
    }

    public ReflectMethod getMethodByNameAndParams(String name, Class<?>... parameterTypes) {
        return this.methods().get(name + Arrays.toString(parameterTypes));
    }

    public Collection<ReflectMethod> getMethodsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.methods().values().stream()
                .filter(method -> method.get().isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public Collection<ReflectMethod> getMethods() {
        return this.methods().values();
    }

    private String getKey(ReflectConstructor<T> constructor) {
        return Arrays.toString(constructor.getParameterTypes());
    }

    @SuppressWarnings("unchecked")
    private Map<String, ReflectConstructor<T>> constructors() {
        if (this.constructors.isEmpty()) {
            Arrays.stream(this.target.getConstructors()).map(ReflectConstructor::new)
                    .map(constructor -> (ReflectConstructor<T>) constructor)
                    .forEach(constructor -> this.constructors.put(getKey(constructor), constructor));
            Arrays.stream(this.target.getDeclaredConstructors()).map(ReflectConstructor::new)
                    .map(constructor -> (ReflectConstructor<T>) constructor)
                    .forEach(constructor -> this.constructors.put(getKey(constructor), constructor));
        }
        return this.constructors;
    }

    // Constructor access methods
    public ReflectConstructor<T> getConstructorWithParams(Class<?>... parameterTypes) {
        return this.constructors().get(Arrays.toString(parameterTypes));
    }

    public Collection<ReflectConstructor<T>> getConstructorsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.constructors().values().stream()
                .filter(constructor -> constructor.get().isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public Collection<ReflectConstructor<T>> getConstructors() {
        return this.constructors().values();
    }

    public ReflectConstructor<T> getConstructor() {
        return this.getConstructors().stream().findFirst().orElse(null);
    }

    private Map<String, ReflectField> fields() {
        if (this.fields.isEmpty()) {
            Arrays.stream(this.target.getFields()).map(ReflectField::new)
                    .forEach(reflectField -> this.fields.put(reflectField.getName(), reflectField));
            Arrays.stream(this.target.getDeclaredFields()).map(ReflectField::new)
                    .forEach(reflectField -> this.fields.put(reflectField.getName(), reflectField));
        }
        return this.fields;
    }

    public Collection<ReflectField> getFields() {
        return this.fields().values();
    }

    public ReflectField getFieldByName(String name) {
        return this.fields().get(name);
    }

    public Collection<ReflectField> getFieldsByType(Class<?> type) {
        return this.getFields().stream()
                .filter(field -> field.get().getType().equals(type))
                .collect(Collectors.toList());
    }

    public Collection<ReflectField> getFieldsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getFields().stream()
                .filter(field -> field.get().isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @Override
    public int modifiers() {
        return this.target.getModifiers();
    }
}