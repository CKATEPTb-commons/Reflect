package dev.ckateptb.reflection;

import java.lang.reflect.Modifier;

public interface ModifierHolder {
    int modifiers();

    default boolean isPublic() {
        return Modifier.isPublic(this.modifiers());
    }

    default boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers());
    }

    default boolean isProtected() {
        return Modifier.isProtected(this.modifiers());
    }

    default boolean isStatic() {
        return Modifier.isStatic(this.modifiers());
    }

    default boolean isFinal() {
        return Modifier.isFinal(this.modifiers());
    }

    default boolean isSynchronized() {
        return Modifier.isSynchronized(this.modifiers());
    }

    default boolean isVolatile() {
        return Modifier.isVolatile(this.modifiers());
    }

    default boolean isTransient() {
        return Modifier.isTransient(this.modifiers());
    }

    default boolean isNative() {
        return Modifier.isNative(this.modifiers());
    }

    default boolean isInterface() {
        return Modifier.isInterface(this.modifiers());
    }

    default boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers());
    }

    default boolean isStrict() {
        return Modifier.isStrict(this.modifiers());
    }
}
