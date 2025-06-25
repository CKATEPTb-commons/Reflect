package dev.ckateptb.reflection.api;

import java.lang.annotation.Annotation;

/**
 * Represents an entity that holds Java annotations and provides
 * methods to query and retrieve annotation instances.
 */
public interface AnnotationHolder {

    /**
     * Checks if the specified annotation type is present on this element.
     *
     * @param annotation the annotation class to look for
     * @return {@code true} if an annotation of the specified type is present; {@code false} otherwise
     */
    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    /**
     * Retrieves the annotation of the specified type if present on this element.
     *
     * @param <A>        the annotation type
     * @param annotation the annotation class to retrieve
     * @return the annotation instance if present, or {@code null} if not present
     */
    <A extends Annotation> A getAnnotation(Class<A> annotation);
}
