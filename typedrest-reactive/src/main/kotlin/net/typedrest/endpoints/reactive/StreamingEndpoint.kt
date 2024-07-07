package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import net.typedrest.endpoints.Endpoint

/**
 * Endpoint for a stream of [TEntity]s.
 *
 * @param TEntity The type of individual elements in the stream.
 */
interface StreamingEndpoint<TEntity : Any> : Endpoint {
    /**
     * Provides an [Observable] stream of entities.
     *
     * @return A cold observable. HTTP communication only starts once [Observable.subscribe] is invoked.
     */
    fun getObservable(): Observable<TEntity>
}
