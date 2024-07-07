package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.typedrest.endpoints.Endpoint
import net.typedrest.endpoints.generic.ElementEndpointImpl
import net.typedrest.http.retryAfterDuration
import okhttp3.Response
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds

/**
 * Endpoint for a resource that can be polled for state changes.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of entity the endpoint represents.
 * @param endCondition A check to determine whether the entity has reached its final state and no further polling is required.
 * @param TEntity The type of entity the endpoint represents.
 */
class PollingEndpointImpl<TEntity : Any>(
    referrer: Endpoint,
    relativeUri: URI,
    entityType: Class<TEntity>,
    private val endCondition: ((TEntity) -> Boolean)? = null
) : ElementEndpointImpl<TEntity>(referrer, relativeUri, entityType), PollingEndpoint<TEntity> {
    /**
     * Creates a new polling endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint represents.
     * @param endCondition A check to determine whether the entity has reached its final state and no further polling is required.
     */
    constructor(
        referrer: Endpoint,
        relativeUri: String,
        entityType: Class<TEntity>,
        endCondition: ((TEntity) -> Boolean)? = null
    ) : this(referrer, URI(relativeUri), entityType, endCondition)

    override fun handle(response: Response): Response {
        val response = super.handle(response)
        response.retryAfterDuration()?.let { pollingInterval = it }
        return response
    }

    override var pollingInterval: Duration = ofSeconds(3)

    override fun getObservable(): Observable<TEntity> =
        Observable.create<TEntity> { emitter ->
            while (!emitter.isDisposed) {
                val entity = read()
                emitter.onNext(entity)
                if (endCondition?.invoke(entity) == true) break

                emitter.sleep(pollingInterval)
            }

            emitter.onComplete()
        }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
}
