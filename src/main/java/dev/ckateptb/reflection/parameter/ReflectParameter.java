package dev.ckateptb.reflection.parameter;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.api.AnnotationHolder;
import dev.ckateptb.reflection.api.ModifierHolder;
import dev.ckateptb.reflection.api.NameHolder;
import dev.ckateptb.reflection.type.IReflectClass;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Reflective representation of a {@link Parameter}, providing
 * both the raw parameter metadata and a reflective handle to its type.
 * <p>
 * Delegates type introspection to an underlying {@link IReflectClass} for the
 * parameter's declared type, while exposing parameter-specific properties.
 * </p>
 *
 * @param <T> the parameter's declared type
 */
public class ReflectParameter<T> implements IReflectClass<T> {
    /**
     * The underlying {@link Parameter} instance.
     */
    @Getter
    private final Parameter raw;
    /**
     * Reflective representation of the parameter's type.
     */
    @Delegate(excludes = {AnnotationHolder.class, ModifierHolder.class, NameHolder.class})
    private final IReflectClass<T> type;

    /**
     * Constructs a new ReflectParameter wrapper around the given raw parameter.
     *
     * @param raw the {@link Parameter} to wrap
     */
    @SuppressWarnings("unchecked")
    public ReflectParameter(Parameter raw) {
        this.raw = raw;
        this.type = (IReflectClass<T>) Reflect.on(raw.getType());
    }

    /**
     * Returns the name of this parameter as declared in the source.
     *
     * @return the declared parameter name
     */
    public String getName() {
        return this.raw.getName();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks if the specified annotation is present on the raw parameter.
     * </p>
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return this.raw.isAnnotationPresent(annotation);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the specified annotation instance from the raw parameter.
     * </p>
     */
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return this.raw.getAnnotation(annotation);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the modifier bitmask of the raw parameter.
     * </p>
     */
    @Override
    public int getModifiers() {
        return this.raw.getModifiers();
    }
}
