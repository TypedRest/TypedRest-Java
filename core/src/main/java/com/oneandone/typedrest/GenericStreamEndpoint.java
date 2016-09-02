package com.oneandone.typedrest;

import rx.util.async.StoppableObservable;

/**
 * REST endpoint that represents a stream of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s.
 *
 * Use the more constrained {@link StreamEndpoint} when possible.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public interface GenericStreamEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends GenericPagedCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Provides an observable stream of elements.
     *
     * @return An observable stream of elements.
     */
    default StoppableObservable<TEntity> getObservable() {
        return getObservable(0);
    }

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream.
     * Use negative values to start counting from the end of the stream.
     * @return An observable stream of elements.
     */
    StoppableObservable<TEntity> getObservable(long startIndex);
}
