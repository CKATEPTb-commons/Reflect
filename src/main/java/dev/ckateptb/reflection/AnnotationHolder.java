package dev.ckateptb.reflection;

import java.lang.annotation.Annotation;

public interface AnnotationHolder {
    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    <A extends Annotation> A annotation(Class<A> annotation);
}
