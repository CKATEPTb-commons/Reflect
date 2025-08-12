package dev.ckateptb.reflection.processor;

import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.util.function.Function;

/**
 * Processor for reflective invocation of a specific {@link Constructor},
 * leveraging {@link MethodHandle} when possible for direct calls,
 * with a fallback to {@link Constructor#newInstance(Object...)}.
 * <p>
 * Instances are cached per {@link Constructor} to avoid repeated lookup
 * and accessibility operations.
 * </p>
 *
 * @param <T> the type produced by the constructor invocation
 */
@RequiredArgsConstructor
public class ConstructorProcessor<T> {
    /**
     * Underlying invocation function accepting constructor arguments.
     */
    private final Function<Object[], T> invoke;

    /**
     * Invokes the underlying constructor with the specified arguments.
     *
     * @param args the arguments for the constructor
     * @return a new instance of type {@code T}
     * @throws RuntimeException if the underlying invocation fails
     */
    public T invoke(Object... args) {
        return this.invoke.apply(args);
    }
}
