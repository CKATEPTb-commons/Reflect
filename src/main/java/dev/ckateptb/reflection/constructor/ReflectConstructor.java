package dev.ckateptb.reflection.constructor;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.method.ContextMethod;
import dev.ckateptb.reflection.method.IReflectMethod;
import dev.ckateptb.reflection.parameter.ReflectParameter;
import dev.ckateptb.reflection.processor.Processor;
import dev.ckateptb.reflection.type.IReflectClass;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link IReflectConstructor}, providing reflective
 * access to a constructor's metadata, parameter inspection, invocation,
 * and return-type handling for creating new instances.
 *
 * <p>Delegates class-level introspection to an underlying {@link IReflectClass}
 * for metadata queries, and uses a cached {@link Processor} to invoke the constructor.</p>
 *
 * @param <T> the type constructed by this constructor
 */
public class ReflectConstructor<T> implements IReflectConstructor<T> {
    /**
     * The underlying raw {@link Constructor} instance.
     */
    @Getter
    private final Constructor<T> raw;
    /**
     * Reflective handle for the constructor's declaring class.
     */
    private final IReflectClass<T> type;

    /**
     * Constructs a new ReflectConstructor wrapper for the given raw constructor.
     *
     * @param raw the constructor to wrap
     */
    public ReflectConstructor(Constructor<T> raw) {
        this.raw = raw;
        this.type = Reflect.on(raw.getDeclaringClass());
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the declaring class reflector.</p>
     */
    @Override
    public Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter) {
        return this.type.getFieldsByFilter(filter);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the declaring class reflector.</p>
     */
    @Override
    public Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.type.getMethodsByFilter(filter);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the declaring class reflector.</p>
     */
    @Override
    public Collection<IReflectConstructor<T>> getConstructorsByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.type.getConstructorsByFilter(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        return this.type.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public IReflectClass<T> invoke(Object... args) {
        return (IReflectClass<T>) Reflect.on(Processor.from(this.raw).invoke(args));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getReturnType() {
        return (IReflectClass<R>) this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ReflectParameter<?>> getParameters(Predicate<ReflectParameter<?>> filter) {
        return Arrays.stream(this.raw.getParameters()).map(ReflectParameter::new).filter(filter)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>Deprecated in favor of {@link #invoke(Object...)}, not supported for constructors.</p>
     */
    @Override
    @Deprecated
    public T getValue() {
        return this.type.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IReflectClass<T> setValue(T value) {
        return this.type.setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.raw.isAnnotationPresent(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.raw.getAnnotation(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return this.raw.getModifiers();
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the constructor
     */
    @Override
    public String getName() {
        return this.raw.getName();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code ReflectConstructor<R>}
     */
    public <R> ReflectConstructor<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code ContextMethod<R>}
     */
    @SuppressWarnings("unchecked")
    public <R> ReflectConstructor<R> cast() {
        return (ReflectConstructor<R>) this;
    }
}
