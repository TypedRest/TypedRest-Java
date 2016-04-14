package com.oneandone.typedrest;

import java.util.function.Predicate;
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
    default StoppableObservable<TEntity> getObservable(Integer pollingInterval) {
        return getObservable(pollingInterval, null);
    }

    /**
     * Provides an observable stream of element states. Compares entities using
     * {@link Object#equals(java.lang.Object)} to detect changes.
     *
     * @param pollingInterval The interval in seconds in which to send requests
     * to the server.
     * @param endCondition An optional predicate determining which entity state
     * ends the polling process.
     * @return An observable stream of element states.
     */
    StoppableObservable<TEntity> getObservable(Integer pollingInterval, Predicate<TEntity> endCondition);
}
