package net.typedrest.http

import okhttp3.Response
import okio.BufferedSource
import java.time.Duration

/**
 * A single event read from a Server-Sent Events (SSE) stream.
 *
 * @param type The event type from the `event:` field. `message` if the server did not specify one.
 * @param data The concatenated content of the event's `data:` fields.
 * @param id The id of the most recent `id:` field if any.
 */
data class ServerSentEvent(val type: String, val data: String, val id: String?)

/**
 * The event type used for events without an explicit `event:` field.
 */
const val DEFAULT_EVENT_TYPE = "message"

/**
 * Reads [ServerSentEvent]s from an HTTP response body in the `text/event-stream` format.
 *
 * @param source The response body to read from.
 */
class SseReader(private val source: BufferedSource) {
    /**
     * The id of the most recent event that carried an `id:` field.
     *
     * Send this back as a `Last-Event-ID` header when reconnecting to let the server resume where it left off.
     */
    var lastEventId: String? = null
        private set

    /**
     * The reconnection interval most recently requested by the server via a `retry:` field if any.
     */
    var reconnectionInterval: Duration? = null
        private set

    /**
     * Reads events until the stream ends.
     *
     * Iterating the sequence blocks while waiting for the server to send more data. Any event that is still incomplete
     * when the stream ends is discarded.
     */
    fun events(): Sequence<ServerSentEvent> = sequence {
        var type: String? = null
        val data = StringBuilder()

        while (true) {
            val line = source.readUtf8Line() ?: break

            if (line.isEmpty()) {
                // A blank line dispatches the event collected so far
                if (data.isNotEmpty()) {
                    // Every data field appended a trailing newline; the last one is not part of the payload
                    yield(ServerSentEvent(type ?: DEFAULT_EVENT_TYPE, data.substring(0, data.length - 1), lastEventId))
                    data.setLength(0)
                }
                type = null
                continue
            }

            // Lines starting with a colon are comments, e.g. the ":ping" some servers send to keep the connection alive
            if (line.startsWith(":")) continue

            val colonIndex = line.indexOf(':')
            val field = if (colonIndex == -1) line else line.substring(0, colonIndex)
            val value = when {
                colonIndex == -1 -> ""
                line.getOrNull(colonIndex + 1) == ' ' -> line.substring(colonIndex + 2)
                else -> line.substring(colonIndex + 1)
            }

            when (field) {
                "event" -> type = value
                "data" -> data.append(value).append('\n')
                "id" -> if (!value.contains(Char(0))) lastEventId = value
                "retry" -> value.toLongOrNull()?.takeIf { it >= 0 }?.let { reconnectionInterval = Duration.ofMillis(it) }
            }
        }
    }
}

/**
 * Reads the response body as a Server-Sent Events (SSE) stream.
 */
fun Response.sseReader(): SseReader = SseReader(body.source())
