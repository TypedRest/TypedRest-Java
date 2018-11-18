package net.typedrest;

import rx.util.async.StoppableObservable;

/**
 * REST endpoint that represents an entity that can be polled for state changes.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface PollingEndpoint<TEntity>
        extends ElementEndpoint<TEntity> {

    /**
     * Gets the interval in which to send requests to the server.
     *
     * The server can modify this value using the "Retry-After" header.
     *
     * @return An interval in seconds.
     */
    int getPollingInterval();

    /**
     * Sets the interval in which to send requests to the server.
     *
     * The server can modify this value using the "Retry-After" header.
     *
     * @param interval An interval in seconds.
     */
    void setPollingInterval(int interval);

    /**
     * Provides an observable stream of element states. Compares entities using
     * {@link Object#equals(java.lang.Object)} to detect changes.
     *
     * @return An observable stream of element states.
     */
    StoppableObservable<TEntity> getObservable();
}
