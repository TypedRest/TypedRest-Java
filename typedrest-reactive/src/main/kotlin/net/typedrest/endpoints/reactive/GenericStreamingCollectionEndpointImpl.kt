package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.typedrest.endpoints.Endpoint
import net.typedrest.endpoints.generic.ElementEndpoint
import net.typedrest.endpoints.generic.GenericCollectionEndpointImpl
import net.typedrest.errors.ConflictException
import net.typedrest.http.retryAfterDuration
import okhttp3.Response
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds

/**
 * Endpoint for a collection of [TEntity]s observable as an append-only stream.
 *
 * Use the more constrained [StreamingCollectionEndpointImpl] when possible.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of individual elements in the collection.
 * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
 * @param TEntity The type of individual elements in the collection.
 * @param TElementEndpoint The type of [ElementEndpoint] to provide for individual [TEntity]s.
 */
open class GenericStreamingCollectionEndpointImpl<TEntity : Any, TElementEndpoint : ElementEndpoint<TEntity>>(
    referrer: Endpoint,
    relativeUri: URI,
    entityType: Class<TEntity>,
    elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint
) : GenericCollectionEndpointImpl<TEntity, TElementEndpoint>(referrer, relativeUri, entityType, elementEndpointFactory),
    GenericStreamingCollectionEndpoint<TEntity, TElementEndpoint> {
    /**
     * Creates a new streaming collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the collection.
     * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>, elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint) :
        this(referrer, URI(relativeUri), entityType, elementEndpointFactory)

    override fun handle(response: Response): Response {
        val response = super.handle(response)
        response.retryAfterDuration()?.let { pollingInterval = it }
        return response
    }

    override var pollingInterval: Duration = ofSeconds(3)

    override fun getObservable(startIndex: Long): Observable<TEntity> =
        Observable.create<TEntity> { emitter ->
            var currentStartIndex: Long = startIndex

            while (!emitter.isDisposed) {
                val response = try {
                    if (currentStartIndex >= 0) readRange(from = currentStartIndex, to = null) // Offset
                    else readRange(from = null, to = -currentStartIndex) // Tail
                } catch (_: ConflictException) {
                    // No new data available yet, keep polling
                    continue
                }

                response.elements.forEach(emitter::onNext)

                if (response.endReached) {
                    emitter.onComplete()
                    return@create
                }

                // Continue polling for more data
                response.range?.to?.let { to -> currentStartIndex = to + 1 }
                    ?: return@create

                emitter.sleep(pollingInterval)
            }
        }
            .subscribeOn(Schedulers.io())
}
