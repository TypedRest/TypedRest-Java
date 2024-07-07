package net.typedrest.endpoints.reactive

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import net.typedrest.endpoints.AbstractEndpoint
import net.typedrest.endpoints.Endpoint
import net.typedrest.http.HttpStatusCode
import net.typedrest.http.SseReader
import net.typedrest.http.parseEntity
import net.typedrest.http.sseReader
import net.typedrest.http.uri
import okhttp3.Request
import java.io.IOException
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds

/**
 * Endpoint for a stream of [TEntity]s using Server-Sent Events (SSE).
 *
 * Sends `Accept: text/event-stream`. By default, transparently reconnects on connection drops or transient errors,
 * honoring the server-supplied `retry:` interval and resuming via the `Last-Event-ID` header.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of individual elements in the stream.
 * @param eventType If set, only events with this `event:` type are emitted; others are ignored. null emits all events.
 * @param TEntity The type of individual elements in the stream.
 */
open class SseStreamingEndpointImpl<TEntity : Any>(
    referrer: Endpoint,
    relativeUri: URI,
    private val entityType: Class<TEntity>,
    private val eventType: String? = null
) : AbstractEndpoint(referrer, relativeUri), StreamingEndpoint<TEntity> {
    /**
     * Creates a new SSE streaming endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the stream.
     * @param eventType If set, only events with this `event:` type are emitted; others are ignored. null emits all events.
     */
    constructor(
        referrer: Endpoint,
        relativeUri: String,
        entityType: Class<TEntity>,
        eventType: String? = null
    ) : this(referrer, URI(relativeUri), entityType, eventType)

    /**
     * Whether to transparently reconnect on connection drops, transient transport errors and 5xx responses.
     */
    var autoReconnect: Boolean = true

    /**
     * The reconnection interval used when the server has not (yet) supplied one via the SSE `retry:` field.
     */
    var defaultReconnectionInterval: Duration = ofSeconds(3)

    override fun getObservable(): Observable<TEntity> = Observable.create<TEntity> { emitter ->
        var lastEventId: String? = null
        var reconnectionInterval = defaultReconnectionInterval

        while (!emitter.isDisposed) {
            val reconnect = try {
                consumeOnce(emitter, lastEventId) { reader ->
                    reader.lastEventId?.let { lastEventId = it }
                    reader.reconnectionInterval?.let { reconnectionInterval = it }
                }
            } catch (ex: IOException) {
                // Transport-level failures are the ones worth retrying; anything else is the server telling us no
                if (!autoReconnect) {
                    emitter.onError(ex)
                    return@create
                }
                true
            } catch (ex: Throwable) {
                emitter.onError(ex)
                return@create
            }

            if (!reconnect) break
            emitter.sleep(reconnectionInterval)
        }

        emitter.onComplete()
    }.subscribeOn(Schedulers.io())

    /**
     * Opens a connection and emits events until the stream ends.
     *
     * @param updateState Called with the reader once the connection has ended, to carry the last event id and reconnection interval over to the next attempt.
     * @return true if another connection attempt should be made, false if the stream has ended for good.
     */
    private fun consumeOnce(emitter: ObservableEmitter<TEntity>, lastEventId: String?, updateState: (SseReader) -> Unit): Boolean {
        val request = Request.Builder()
            .get()
            .uri(uri)
            .header("Accept", EVENT_STREAM_MEDIA_TYPE)
            .apply { if (!lastEventId.isNullOrEmpty()) header("Last-Event-ID", lastEventId) }
            .build()

        val call = httpClient.newCall(request)
        emitter.setCancellable(call::cancel)
        val response = call.execute()

        if (response.code == HttpStatusCode.NoContent.code) {
            response.close()
            return false
        }
        if (autoReconnect && response.code >= 500) {
            response.close()
            return true
        }

        handle(response).use {
            val reader = it.sseReader()
            try {
                val serializer = serializers.first()
                for (event in reader.events()) {
                    if (emitter.isDisposed) return false
                    if (eventType != null && event.type != eventType) continue
                    parseEntity(serializer, event.data.toByteArray(), entityType)?.let(emitter::onNext)
                }
            } finally {
                updateState(reader)
            }
        }

        return autoReconnect
    }

    private companion object {
        const val EVENT_STREAM_MEDIA_TYPE = "text/event-stream"
    }
}
