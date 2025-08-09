package dev.ckateptb.reflection.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstructorProcessor<T> implements Processor {
    /**
     * Cache of ConstructorProcessor instances keyed by Constructor.
     */
    private static final Map<Constructor<?>, ConstructorProcessor<?>> PROCESSORS = new ConcurrentHashMap<>();

    /**
     * Creates or retrieves a cached {@link ConstructorProcessor} for the given constructor.
     * <p>
     * Attempts to look up a {@link MethodHandle} for the constructor via private lookup,
     * falling back to reflective {@code newInstance} if access handling fails.
     * </p>
     *
     * @param constructor the {@link Constructor} to process
     * @param <T>         the type constructed
     * @return a cached ConstructorProcessor configured for the given constructor
     */
    @SuppressWarnings("unchecked")
    static <T> ConstructorProcessor<T> from(Constructor<?> constructor) {
        return (ConstructorProcessor<T>) PROCESSORS.computeIfAbsent(constructor, key -> {
            constructor.trySetAccessible();
            try {
                MethodHandle handle = MethodHandles.privateLookupIn(constructor.getDeclaringClass(), MethodHandles.lookup())
                        .findConstructor(
                                constructor.getDeclaringClass(),
                                MethodType.methodType(void.class, constructor.getParameterTypes())
                        );
                return new ConstructorProcessor<>(
                        (args) -> {
                            try {
                                return (T) handle.invokeWithArguments(args);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            } catch (IllegalAccessException | NoSuchMethodException e) {
                return new ConstructorProcessor<>(
                        (args) -> {
                            try {
                                return constructor.newInstance(args);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                );
            }
        });
    }

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
