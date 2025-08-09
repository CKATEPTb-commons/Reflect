package dev.ckateptb.reflection.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Processor for invoking a specific {@link Method} via reflection,
 * using either {@link MethodHandle} for private access or
 * conventional {@link Method#invoke(Object, Object...)} as fallback.
 * <p>
 * Instances are cached per {@link Method} to avoid repeated handle lookups
 * and accessibility checks.
 * </p>
 *
 * @param <T> the return type of the method invocation
 */

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodProcessor<T> implements Processor {
    /**
     * Cache of MethodProcessor instances keyed by Method.
     */
    private static final Map<Method, MethodProcessor<?>> PROCESSORS = new ConcurrentHashMap<>();

    /**
     * Creates or retrieves a cached {@link MethodProcessor} for the given method.
     * <p>
     * Attempts to obtain a {@link MethodHandle} for the method via a private lookup,
     * falling back to standard reflective invocation if access fails.
     * </p>
     *
     * @param method the {@link Method} to process
     * @param <T>    the expected return type of the method
     * @return a cached MethodProcessor configured for the given method
     */
    @SuppressWarnings("unchecked")
    static <T> MethodProcessor<T> from(Method method) {
        return (MethodProcessor<T>) PROCESSORS.computeIfAbsent(method, key -> {
            method.trySetAccessible();
            try {
                MethodHandle handle = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup())
                        .unreflect(method);
                return Modifier.isStatic(method.getModifiers()) ? new MethodProcessor<>(
                        (target, args) -> {
                            try {
                                return (T) handle.invokeWithArguments(args);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }) : new MethodProcessor<>(
                        (target, args) -> {
                            try {
                                return (T) handle.bindTo(target).invokeWithArguments(args);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            } catch (IllegalAccessException e) {
                return new MethodProcessor<>(
                        (target, args) -> {
                            if (!method.canAccess(target)) method.setAccessible(true);
                            try {
                                return method.invoke(target, args);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                );
            }
        });
    }

    /**
     * Underlying invocation function that binds a target and arguments.
     */
    private final BiFunction<Object, Object[], T> invoke;

    /**
     * Invokes the underlying method processor on the given target instance
     * with the specified arguments.
     *
     * @param target the instance on which to invoke the method (or {@code null} for static methods)
     * @param args   the arguments to pass to the method
     * @return the result of the method invocation
     * @throws RuntimeException if the underlying invocation fails
     */
    public T invoke(Object target, Object... args) {
        return this.invoke.apply(target, args);
    }
}
