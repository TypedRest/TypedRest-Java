package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.typedrest.endpoints.Endpoint
import net.typedrest.endpoints.AbstractEndpoint
import net.typedrest.http.entitySequence
import net.typedrest.http.uri
import okhttp3.Request
import okio.ByteString.Companion.encodeUtf8
import java.net.URI

/**
 * Endpoint for a stream of [TEntity]s using a persistent HTTP connection.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of individual elements in the stream.
 * @param separator The character sequence used to detect that a new element starts in an HTTP stream.
 * @param TEntity The type of individual elements in the stream.
 */
class StreamingEndpointImpl<TEntity : Any>(
    referrer: Endpoint,
    relativeUri: URI,
    private val entityType: Class<TEntity>,
    private val separator: String = "\n"
) : AbstractEndpoint(referrer, relativeUri), StreamingEndpoint<TEntity> {
    /**
     * Creates a new streaming endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the stream.
     * @param separator The character sequence used to detect that a new element starts in an HTTP stream.
     */
    constructor(
        referrer: Endpoint,
        relativeUri: String,
        entityType: Class<TEntity>,
        separator: String = "\n"
    ) : this(referrer, URI(relativeUri), entityType, separator)

    /**
     * The size of the buffer used to collect data for deserialization in bytes.
     */
    var bufferSize: Int = DEFAULT_BUFFER_SIZE

    override fun getObservable(): Observable<TEntity> = Observable.create<TEntity> { emitter ->
        val call = httpClient.newCall(Request.Builder().get().uri(uri).build())
        emitter.setCancellable(call::cancel)

        call.execute()
            .use { response ->
                response
                    .entitySequence(serializers.first(), separator.encodeUtf8(), bufferSize, entityType)
                    .takeWhile { !emitter.isDisposed }
                    .forEach(emitter::onNext)
            }

        emitter.onComplete()
    }.subscribeOn(Schedulers.io())
}
