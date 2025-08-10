package dev.ckateptb.reflection.flag;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks a set of bitwise {@link Flag flags} in a single {@code int} using
 * a lock-free, thread-safe {@link AtomicInteger}.
 *
 * <p>Each {@link Flag} exposes its bit {@linkplain Flag#getMask() mask}.
 * This tracker stores the union of all enabled masks in an internal
 * atomic integer.</p>
 *
 * <h2>Thread-safety</h2>
 * <ul>
 *   <li>All updates are atomic and visible to other threads thanks to {@link AtomicInteger}.</li>
 *   <li>Calling {@link #addFlag(Flag)} is idempotent: enabling an already-enabled flag has no effect.</li>
 *   <li>Reads via {@link #hasFlag(Flag)} observe the latest committed value with standard
 *       {@code volatile}-like visibility guarantees of {@link AtomicInteger#get()}.</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * FlagTracker tracker = new FlagTracker();
 * tracker.addFlag(Flag.CLASSES_CACHED);
 * if (tracker.hasFlag(Flag.CLASSES_CACHED)) {
 *     // classes cache is initialized
 * }
 * }</pre>
 *
 * @see Flag
 */
public class FlagTracker {
    /**
     * Atomic bitmask that stores all enabled flags.
     * <p>Initial value is {@code 0} (no flags enabled).</p>
     */
    private final AtomicInteger flags = new AtomicInteger(0);

    /**
     * Checks whether the specified flag has been set.
     *
     * @param flag the flag to test
     * @return {@code true} if the flag is set; {@code false} otherwise
     */
    protected boolean hasFlag(Flag flag) {
        return (this.flags.get() & flag.getMask()) != 0;
    }

    /**
     * Sets the specified flag.
     *
     * @param flag the flag to set
     */
    protected void addFlag(Flag flag) {
        this.flags.getAndUpdate(flags -> flags | flag.getMask());
    }
}
