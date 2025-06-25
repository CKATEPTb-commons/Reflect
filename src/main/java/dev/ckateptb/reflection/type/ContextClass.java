package dev.ckateptb.reflection.type;


import dev.ckateptb.reflection.constructor.IReflectConstructor;
import dev.ckateptb.reflection.field.ContextField;
import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.method.ContextMethod;
import dev.ckateptb.reflection.method.IReflectMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contextual implementation of {@link IReflectClass} that binds
 * a specific instance value to reflective operations.
 * <p>
 * Delegates class-level introspection to a {@link ReflectClass} while
 * returning context-aware field and method wrappers that operate on the bound instance.
 * </p>
 *
 * @param <T> the type of the bound instance
 */
@RequiredArgsConstructor
public class ContextClass<T> implements IReflectClass<T> {
    /**
     * Underlying class reflector to delegate introspection logic.
     */
    private final ReflectClass<T> delegate;
    /**
     * The bound instance value used by context-aware fields and methods.
     */
    @Getter
    private final T value;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        return this.delegate.getType();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns context-wrapped fields for the bound instance.
     * </p>
     */
    @Override
    public synchronized Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter) {
        return this.delegate.getFieldsByFilter(filter).stream()
                .map(field -> new ContextField<>(field.getRaw(), this))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns context-wrapped methods for the bound instance.
     * </p>
     */
    @Override
    public Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.delegate.getMethodsByFilter(filter).stream()
                .map(method -> new ContextMethod<>(method.getRaw(), this))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates constructor retrieval; constructors are not context-bound.
     * </p>
     */
    @Override
    public Collection<IReflectConstructor<T>> getConstructorsByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.delegate.getConstructorsByFilter(filter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates value binding to the underlying reflector.
     * </p>
     */
    @Override
    public IReflectClass<T> setValue(T instance) {
        return this.delegate.setValue(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return this.delegate.getModifiers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.delegate.isAnnotationPresent(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.delegate.getAnnotation(annotation);
    }
}
