package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import net.typedrest.endpoints.generic.ElementEndpoint
import net.typedrest.endpoints.generic.GenericCollectionEndpoint
import java.time.Duration

/**
 * Endpoint for a collection of [TEntity]s observable as an append-only stream.
 *
 * Use the more constrained [StreamingCollectionEndpoint] when possible.
 *
 * @param TEntity The type of individual elements in the collection.
 * @param TElementEndpoint The type of [ElementEndpoint] to provide for individual [TEntity]s.
 */
interface GenericStreamingCollectionEndpoint<TEntity : Any, TElementEndpoint : ElementEndpoint<TEntity>>
    : GenericCollectionEndpoint<TEntity, TElementEndpoint> {
    /**
     * Interval in which requests are sent to the server.
     * The server may update this interval via the `Retry-After` response header.
     */
    var pollingInterval: Duration

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream. Use negative values to start counting from the end of the stream.
     * @return A cold observable. HTTP communication only starts once [Observable.subscribe] is invoked
     */
    fun getObservable(startIndex: Long = 0): Observable<TEntity>
}
