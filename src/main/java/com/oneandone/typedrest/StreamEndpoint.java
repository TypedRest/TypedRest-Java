package com.oneandone.typedrest;

import rx.Observable;

/**
 * REST endpoint that represents a stream of entities.
 *
 * @param <TElement> The type of elements the endpoint represents.
 */
public interface StreamEndpoint<TElement> extends PaginationEndpoint<TElement> {

    /**
     * Provides an observable stream of elements.
     *
     * @return An observable stream of elements.
     */
    Observable<TElement> getObservable();

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream.
     * Use negative values to start counting from the end of the stream.
     * @return An observable stream of elements.
     */
    Observable<TElement> getObservable(long startIndex);
}
