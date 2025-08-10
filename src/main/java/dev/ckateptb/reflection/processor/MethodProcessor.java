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

@RequiredArgsConstructor
public class MethodProcessor<T> {
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
