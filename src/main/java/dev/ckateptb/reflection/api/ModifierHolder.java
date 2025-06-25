package dev.ckateptb.reflection.api;

import java.lang.reflect.Modifier;

/**
 * Represents an entity that holds Java modifiers and provides
 * utility methods to query common modifier properties.
 */
public interface ModifierHolder {
    /**
     * Retrieves the raw integer bitmask of Java modifiers for this element.
     *
     * @return the modifier bitmask as defined in {@link java.lang.reflect.Modifier}
     */
    int getModifiers();

    /**
     * Checks if the {@code public} modifier is present.
     *
     * @return {@code true} if this element is declared public; {@code false} otherwise.
     */
    default boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }

    /**
     * Checks if the {@code private} modifier is present.
     *
     * @return {@code true} if this element is declared private; {@code false} otherwise.
     */
    default boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Checks if the {@code protected} modifier is present.
     *
     * @return {@code true} if this element is declared protected; {@code false} otherwise.
     */
    default boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Checks if the {@code static} modifier is present.
     *
     * @return {@code true} if this element is declared static; {@code false} otherwise.
     */
    default boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * Checks if the {@code final} modifier is present.
     *
     * @return {@code true} if this element is declared final; {@code false} otherwise.
     */
    default boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }

    /**
     * Checks if the {@code synchronized} modifier is present.
     *
     * @return {@code true} if this element is declared synchronized; {@code false} otherwise.
     */
    default boolean isSynchronized() {
        return Modifier.isSynchronized(this.getModifiers());
    }

    /**
     * Checks if the {@code volatile} modifier is present.
     *
     * @return {@code true} if this element is declared volatile; {@code false} otherwise.
     */
    default boolean isVolatile() {
        return Modifier.isVolatile(this.getModifiers());
    }

    /**
     * Checks if the {@code transient} modifier is present.
     *
     * @return {@code true} if this element is declared transient; {@code false} otherwise.
     */
    default boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }

    /**
     * Checks if the {@code native} modifier is present.
     *
     * @return {@code true} if this element is declared native; {@code false} otherwise.
     */
    default boolean isNative() {
        return Modifier.isNative(this.getModifiers());
    }

    /**
     * Checks if the {@code interface} modifier is present.
     *
     * @return {@code true} if this element is an interface; {@code false} otherwise.
     */
    default boolean isInterface() {
        return Modifier.isInterface(this.getModifiers());
    }

    /**
     * Checks if the {@code abstract} modifier is present.
     *
     * @return {@code true} if this element is declared abstract; {@code false} otherwise.
     */
    default boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }

    /**
     * Checks if the {@code strictfp} modifier is present.
     *
     * @return {@code true} if this element is declared strictfp; {@code false} otherwise.
     */
    default boolean isStrict() {
        return Modifier.isStrict(this.getModifiers());
    }
}