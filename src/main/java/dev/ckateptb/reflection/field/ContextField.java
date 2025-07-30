package dev.ckateptb.reflection.field;

import dev.ckateptb.reflection.type.IReflectClass;

import java.lang.reflect.Field;

/**
 * Contextual wrapper for a field, binding it to a specific instance context.
 * <p>
 * Extends {@link ReflectField} to provide a field reflector whose
 * declaring class is the context owner, allowing instance-bound get/set operations.
 * </p>
 *
 * @param <T> the type of the field value
 */
public class ContextField<T> extends ReflectField<T> {

    /**
     * Reflective handle of the owning class, bound to a specific instance.
     */
    private final IReflectClass<?> owner;

    /**
     * Constructs a new ContextField with the given raw field and owning context.
     *
     * @param raw   the raw {@link Field} to wrap
     * @param owner the {@link IReflectClass} representing the context owner
     */

    public ContextField(Field raw, IReflectClass<?> owner) {
        super(raw);
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the context-bound declaring class instead of using the field's
     * original declaring class directly.
     * </p>
     *
     * @param <R> the type of the declaring class context
     * @return the owner context as {@link IReflectClass}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getDeclaringClass() {
        return (IReflectClass<R>) this.owner;
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code ContextField<R>}
     */
    public <R> ContextField<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code ContextField<R>}
     */
    @SuppressWarnings("unchecked")
    public <R> ContextField<R> cast() {
        return (ContextField<R>) this;
    }
}
