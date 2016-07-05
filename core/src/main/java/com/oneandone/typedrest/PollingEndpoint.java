package com.oneandone.typedrest;

import rx.util.async.StoppableObservable;

/**
 * REST endpoint that represents an entity that can be polled for state changes.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface PollingEndpoint<TEntity>
        extends ElementEndpoint<TEntity> {

    /**
     * Provides an observable stream of element states. Compares entities using
     * {@link Object#equals(java.lang.Object)} to detect changes.
     *
     * @param pollingInterval The interval in seconds in which to send requests
     * to the server.
     * @return An observable stream of element states.
     */
    StoppableObservable<TEntity> getObservable(Integer pollingInterval);
}
