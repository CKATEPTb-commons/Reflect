package dev.ckateptb.reflection.flag;

import lombok.RequiredArgsConstructor;

/**
 * A set of bit flags used to track cache initialization states in the library.
 * <p>
 * Each enum constant represents an independent flag with a unique bit
 * {@linkplain #mask mask}. Flags can be combined using bitwise OR and
 * checked using bitwise AND.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * int flags = 0;
 * // Enable "classes cached"
 * flags |= Flag.CLASSES_CACHED.getMask();
 *
 * // Check whether "methods cached" is enabled
 * boolean methodsReady = (flags & Flag.METHODS_CACHED.getMask()) != 0;
 * }</pre>
 *
 */
@RequiredArgsConstructor
public enum Flag {
    /**
     * Flag indicating fields have been cached.
     */
    FIELDS_CACHED(1),
    /**
     * Flag indicating methods have been cached.
     */
    METHODS_CACHED(1 << 1),
    /**
     * Flag indicating constructors have been cached.
     */
    CONSTRUCTORS_CACHED(1 << 2),
    /**
     * Flag indicating classes have been cached.
     */
    CLASSES_CACHED(1 << 3);
    private final int mask;

    /**
     * Retrieves the current bit-mask value.
     *
     * @return The current bit-mask value.
     */
    public int getMask() {
        return this.mask;
    }
}
