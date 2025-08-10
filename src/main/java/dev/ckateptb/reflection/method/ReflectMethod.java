package dev.ckateptb.reflection.method;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.atomic.AtomicOnce;
import dev.ckateptb.reflection.constructor.IReflectConstructor;
import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.parameter.ReflectParameter;
import dev.ckateptb.reflection.processor.MethodProcessor;
import dev.ckateptb.reflection.type.IReflectClass;
import dev.ckateptb.reflection.type.ReflectClass;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link IReflectMethod}, providing reflective
 * access to a method's metadata, parameter inspection, invocation,
 * and return-type handling within optionally bound contexts.
 *
 * @param <T> the return type of the method when invoked
 */
public class ReflectMethod<T> implements IReflectMethod<T> {
    /**
     * The underlying raw {@link Method} instance.
     */
    @Getter
    private final Method raw;

    /**
     * Reflective representation of the method's return type, used for chaining.
     */
    private final ReflectClass<T> type;

    /**
     * Lazy, thread-safe holder of the {@link MethodProcessor} instance.
     * <p>
     * Computed at most once: the first caller runs the supplier synchronously;
     * concurrent callers wait and reuse the same instance. No background threads.
     * </p>
     * <p>Usage: {@code processor.getOrCompute(() -> MethodProcessor.from(method));}</p>
     */
    private final AtomicOnce<MethodProcessor<T>> processor = new AtomicOnce<>();

    /**
     * Constructs a new ReflectMethod for the specified raw method.
     *
     * @param raw the method to wrap
     */
    @SuppressWarnings("unchecked")
    public ReflectMethod(Method raw) {
        this.raw = raw;
        this.type = (ReflectClass<T>) Reflect.on(raw.getReturnType());
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the method
     */
    public String getName() {
        return this.raw.getName();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getDeclaringClass() {
        return (IReflectClass<R>) Reflect.on(this.raw.getDeclaringClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public IReflectClass<T> invoke(Object... args) {
        return Reflect.on(this.processor.getOrCompute(() -> {
            this.raw.trySetAccessible();
            try {
                MethodHandle handle = MethodHandles.privateLookupIn(this.raw.getDeclaringClass(), MethodHandles.lookup())
                        .unreflect(this.raw);
                return Modifier.isStatic(this.raw.getModifiers()) ? new MethodProcessor<>(
                        (target, params) -> {
                            try {
                                return (T) handle.invokeWithArguments(params);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }) : new MethodProcessor<>(
                        (target, params) -> {
                            try {
                                return (T) handle.bindTo(target).invokeWithArguments(params);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            } catch (IllegalAccessException e) {
                return new MethodProcessor<>(
                        (target, params) -> {
                            if (!this.raw.canAccess(target)) this.raw.setAccessible(true);
                            try {
                                return (T) this.raw.invoke(target, params);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                );
            }
        }).invoke(this.getDeclaringClass().getValue(), args));
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
     */
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getReturnType() {
        return (IReflectClass<R>) Reflect.on(this.getType());
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ReflectParameter<?>> getParameters(Predicate<ReflectParameter<?>> filter) {
        return Arrays.stream(this.raw.getParameters()).map(ReflectParameter::new).filter(filter)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter) {
        return this.type.getFieldsByFilter(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.type.getMethodsByFilter(filter);
    }

    /**
     * {@inheritDoc}
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
     *
     * @deprecated use {@link #invoke(Object...)} instead
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
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code ReflectMethod<R>}
     */
    public <R> ReflectMethod<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code ReflectMethod<R>}
     */
    @SuppressWarnings("unchecked")
    public <R> ReflectMethod<R> cast() {
        return (ReflectMethod<R>) this;
    }
}
