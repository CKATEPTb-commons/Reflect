package dev.ckateptb.reflection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Modifier;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReflectWrapper<T> {
    protected final T target;

    public T get() {
        return this.target;
    }

    public abstract int modifiers();

    public boolean isPublic() {
        return Modifier.isPublic(this.modifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.modifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifiers());
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.modifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(this.modifiers());
    }

    public boolean isTransient() {
        return Modifier.isTransient(this.modifiers());
    }

    public boolean isNative() {
        return Modifier.isNative(this.modifiers());
    }

    public boolean isInterface() {
        return Modifier.isInterface(this.modifiers());
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers());
    }

    public boolean isStrict() {
        return Modifier.isStrict(this.modifiers());
    }
}
