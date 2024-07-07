package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import net.typedrest.endpoints.generic.ElementEndpoint
import java.time.Duration

/**
 * Endpoint for a resource that can be polled for state changes.
 *
 * @param TEntity The type of entity the endpoint represents.
 */
interface PollingEndpoint<TEntity : Any> : ElementEndpoint<TEntity> {
    /**
     * Interval in which requests are sent to the server.
     * The server may update this interval via the `Retry-After` response header.
     */
    var pollingInterval: Duration

    /**
     * Returns an [Observable] stream of entity states.
     * Consecutive items are emitted only when the server-supplied entity
     * differs from the previously emitted one according to [Any.equals].
     */
    fun getObservable(): Observable<TEntity>
}
