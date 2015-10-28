package com.oneandone.typedrest;

import rx.Observable;

/**
 * REST endpoint that represents a stream of <code>TEntity</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElement> The specific type of {@link ElementEndpoint} to provide for
 * individual <code>TEntity</code>s.
 */
public interface StreamEndpoint<TEntity, TElement extends ElementEndpoint<TEntity>>
        extends PagedCollectionEndpoint<TEntity, TElement> {

    /**
     * Provides an observable stream of elements.
     *
     * @return An observable stream of elements.
     */
    Observable<TEntity> getObservable();

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream.
     * Use negative values to start counting from the end of the stream.
     * @return An observable stream of elements.
     */
    Observable<TEntity> getObservable(long startIndex);
}
