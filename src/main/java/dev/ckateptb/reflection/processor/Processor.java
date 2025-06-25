package dev.ckateptb.reflection.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Central entry point for obtaining processors that handle reflective
 * invocation or access of class members (fields, methods, constructors).
 * <p>
 * Provides static factory methods to retrieve cached {@link FieldProcessor},
 * {@link MethodProcessor}, or {@link ConstructorProcessor} instances
 * for the given reflective member.
 * </p>
 */
public interface Processor {
    /**
     * Retrieves a cached {@link FieldProcessor} for the specified field.
     *
     * @param field the {@link Field} to process
     * @param <T>   the expected value type of the field
     * @return a {@link FieldProcessor} configured for the field
     */
    static <T> FieldProcessor<T> from(Field field) {
        return FieldProcessor.from(field);
    }

    /**
     * Retrieves a cached {@link MethodProcessor} for the specified method.
     *
     * @param method the {@link Method} to process
     * @param <T>    the expected return type of the method
     * @return a {@link MethodProcessor} configured for the method
     */
    static <T> MethodProcessor<T> from(Method method) {
        return MethodProcessor.from(method);
    }

    /**
     * Retrieves a cached {@link ConstructorProcessor} for the specified constructor.
     *
     * @param constructor the {@link Constructor} to process
     * @param <T>         the type constructed by the constructor
     * @return a {@link ConstructorProcessor} configured for the constructor
     */
    static <T> ConstructorProcessor<T> from(Constructor<?> constructor) {
        return ConstructorProcessor.from(constructor);
    }
}
