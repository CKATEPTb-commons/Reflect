package dev.ckateptb.reflection.atomic;

import lombok.SneakyThrows;

import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A lock-free, single-assignment lazy initializer.
 * <p>
 * {@code AtomicOnce} computes a value at most once. The first thread that wins the
 * CAS installs a {@link FutureTask} and executes it synchronously in the same thread.
 * All concurrent callers will wait on the same {@code FutureTask} and receive the
 * computed value when it completes.
 * </p>
 *
 * <h2>Thread-safety &amp; semantics</h2>
 * <ul>
 *   <li>Computation is performed at most once per instance.</li>
 *   <li>No thread switching: the winner thread runs {@code Supplier#get()} in-place.</li>
 *   <li>Loser threads block on {@link FutureTask#get()} until the result is ready.</li>
 *   <li>Results are safely published to all threads via the {@link AtomicReference}.</li>
 * </ul>
 *
 * <h2>Caveats</h2>
 * <ul>
 *   <li><b>Non-reentrant:</b> The supplied {@code Supplier} must not call
 *       {@code getOrCompute} on the <i>same</i> {@code AtomicOnce} again; doing so would
 *       wait on its own computation and can deadlock.</li>
 *   <li>Exceptions thrown by the supplier are captured by the {@code FutureTask} and will be
 *       rethrown from {@code getOrCompute()} as unchecked (due to {@link SneakyThrows}) —
 *       typically an {@link java.util.concurrent.ExecutionException} wrapping the original cause.</li>
 *   <li>{@code null} results are allowed and will be cached.</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * AtomicOnce<Connection> once = new AtomicOnce<>();
 * Connection c = once.getOrCompute(() -> openConnection(config));
 * }</pre>
 *
 * @param <T> type of the lazily computed value
 */
public final class AtomicOnce<T> {
    /**
     * A thread-safe way to compute and cache the result of a supplier.
     */
    private final AtomicReference<FutureTask<T>> ref = new AtomicReference<>();

    /**
     * Returns the cached result or computes it if not already done.
     *
     * @param sup the supplier to compute the value with
     * @return the computed result
     */
    @SneakyThrows
    public T getOrCompute(Supplier<? extends T> sup) {
        FutureTask<T> future = this.ref.get();
        if (future == null) {
            FutureTask<T> created = new FutureTask<>(sup::get);
            if (this.ref.compareAndSet(null, created)) {
                created.run();
                future = created;
            } else {
                future = this.ref.get();
            }
        }
        return future.get();
    }
}

