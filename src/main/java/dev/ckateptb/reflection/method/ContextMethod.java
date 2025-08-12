package dev.ckateptb.reflection.method;

import dev.ckateptb.reflection.type.IReflectClass;

import java.lang.reflect.Method;

/**
 * Contextual wrapper for a method, binding it to a specific instance context.
 * <p>
 * Extends {@link ReflectMethod} to provide a method reflector whose
 * declaring class is the context owner, allowing instance-bound invocation and
 * reflective inspection on the bound instance.
 * </p>
 *
 * @param <T> the return type of the method when invoked
 */
public class ContextMethod<T> extends ReflectMethod<T> {
    /**
     * Reflective handle of the owning class, bound to a specific instance.
     */
    private final IReflectClass<?> owner;

    /**
     * Constructs a new ContextMethod for the given raw method and owning context.
     *
     * @param raw   the raw {@link Method} to wrap
     * @param owner the {@link IReflectClass} representing the context owner
     */
    public ContextMethod(Method raw, IReflectClass<?> owner) {
        super(raw);
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the context-bound declaring class instead of using the method's
     * original declaring class directly.
     * </p>
     *
     * @param <R> the type of the declaring class context
     * @return the owner context as {@link IReflectClass}
     */
    @SuppressWarnings("unchecked")
    public <R> IReflectClass<R> getDeclaringClass() {
        return (IReflectClass<R>) this.owner;
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code ContextMethod<R>}
     */
    public <R> ContextMethod<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code ContextMethod<R>}
     */
    @SuppressWarnings("unchecked")
    public <R> ContextMethod<R> cast() {
        return (ContextMethod<R>) this;
    }
}
