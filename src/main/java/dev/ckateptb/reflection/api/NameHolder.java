package dev.ckateptb.reflection.api;

/**
 * Represents an entity that holds a name.
 * <p>
 * Implementations of this interface provide a way to retrieve the name of the target.
 * </p>
 */
public interface NameHolder {
    /**
     * Retrieves the name of this target.
     *
     * @return the target name
     */
    String getName();
}
