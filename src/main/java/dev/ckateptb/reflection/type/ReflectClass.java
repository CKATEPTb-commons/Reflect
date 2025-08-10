package dev.ckateptb.reflection.type;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.constructor.IReflectConstructor;
import dev.ckateptb.reflection.constructor.ReflectConstructor;
import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.field.ReflectField;
import dev.ckateptb.reflection.flag.Flag;
import dev.ckateptb.reflection.flag.FlagTracker;
import dev.ckateptb.reflection.method.IReflectMethod;
import dev.ckateptb.reflection.method.ReflectMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link IReflectClass}, providing cached
 * reflective access to fields, methods, and constructors of a class type {@code T}.
 *
 * @param <T> the type being reflected
 */
@RequiredArgsConstructor
public class ReflectClass<T> extends FlagTracker implements IReflectClass<T> {
    /**
     * The underlying {@link Class} object reflected by this instance.
     */
    @Getter
    protected final Class<T> type;
    /**
     * Cache for reflective field wrappers.
     */
    private final Map<Field, ReflectField<?>> fields = new HashMap<>(); // Кеш полей
    /**
     * Cache for reflective method wrappers.
     */
    private final Map<Method, ReflectMethod<?>> methods = new HashMap<>();
    /**
     * Cache for reflective constructor wrappers.
     */
    private final Map<Constructor<T>, ReflectConstructor<T>> constructors = new HashMap<>();


    /**
     * {@inheritDoc}
     * <p>
     * Populates and filters the cached fields, including inherited ones, on first access.
     * </p>
     */
    public synchronized Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter) {
        if (!this.hasFlag(Flag.FIELDS_CACHED)) {
            synchronized (this.fields) {
                if (!this.hasFlag(Flag.FIELDS_CACHED)) {
                    Class<? super T> superclass = this.type.getSuperclass();
                    for (Field field : this.type.getDeclaredFields()) {
                        this.fields.computeIfAbsent(field, ReflectField::new);
                    }
                    if (superclass != null) {
                        Reflect.on(superclass).getFields().forEach(field -> {
                            if (field instanceof ReflectField<?>) {
                                this.fields.computeIfAbsent(field.getRaw(), (key) -> (ReflectField<?>) field);
                            }
                        });
                    }
                    this.addFlag(Flag.FIELDS_CACHED);
                }
            }
        }
        return this.fields.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Populates and filters the cached methods, including inherited and interface methods, on first access.
     * </p>
     */
    public synchronized Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter) {
        if (!this.hasFlag(Flag.METHODS_CACHED)) {
            synchronized (this.methods) {
                if (!this.hasFlag(Flag.METHODS_CACHED)) {
                    Class<? super T> superclass = this.type.getSuperclass();
                    for (Method method : this.type.getDeclaredMethods()) {
                        this.methods.computeIfAbsent(method, ReflectMethod::new);
                    }
                    if (superclass != null) {
                        Reflect.on(superclass).getMethodsByFilter(ignored -> true).forEach(method -> {
                            if (method instanceof ReflectMethod<?>) {
                                this.methods.computeIfAbsent(method.getRaw(), (key) -> (ReflectMethod<?>) method);
                            }
                        });
                    }
                    for (Class<?> clazz : this.type.getInterfaces()) {
                        Reflect.on(clazz).getMethodsByFilter(ignored -> true).forEach(method -> {
                            if (method instanceof ReflectMethod<?>) {
                                this.methods.computeIfAbsent(method.getRaw(), (key) -> (ReflectMethod<?>) method);
                            }
                        });
                    }
                    this.addFlag(Flag.METHODS_CACHED);
                }
            }
        }
        return this.methods.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Populates and filters the cached constructors on first access.
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<IReflectConstructor<T>> getConstructorsByFilter(Predicate<IReflectConstructor<T>> filter) {
        if (!this.hasFlag(Flag.CONSTRUCTORS_CACHED)) {
            synchronized (this.constructors) {
                if (!this.hasFlag(Flag.CONSTRUCTORS_CACHED)) {
                    for (Constructor<T> constructor : (Constructor<T>[]) this.type.getDeclaredConstructors()) {
                        this.constructors.computeIfAbsent(constructor, ReflectConstructor::new);
                    }
                    this.addFlag(Flag.CONSTRUCTORS_CACHED);
                }
            }
        }
        return this.constructors.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the bound instance value, or {@code null} if none set.
     * </p>
     */
    @Override
    public T getValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public IReflectClass<T> setValue(T instance) {
        return new ContextClass<>(this, instance);
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the class
     */
    @Override
    public String getName() {
        return this.type.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return this.type.getModifiers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.type.isAnnotationPresent(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.type.getAnnotation(annotation);
    }

    /**
     * Returns the simple name of the underlying class as given in the
     * source code. Returns an empty string if the underlying class is
     * anonymous.
     *
     * <p>The simple name of an array is the simple name of the
     * component type with "[]" appended.  In particular the simple
     * name of an array whose component type is anonymous is "[]".
     *
     * @return the simple name of the underlying class
     */
    @Override
    public String getSimpleName() {
        return this.type.getSimpleName();
    }
}
