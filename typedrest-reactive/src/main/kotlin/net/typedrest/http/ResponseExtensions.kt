package net.typedrest.http

import net.typedrest.serializers.Serializer
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.ByteString
import java.time.*
import java.time.Duration.ofSeconds
import java.time.format.DateTimeFormatter

/**
 * Returns the relative waiting time extracted from the Retry-After` header if any.
 */
fun Response.retryAfterDuration(): Duration? =
    header("Retry-After")
        ?.let { value ->
            // A plain integer => seconds
            value.toLongOrNull()
                ?.takeIf { it > 0 }
                ?.let(::ofSeconds)
            // Otherwise try RFC-1123 date
                ?: runCatching {
                    val date = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME)
                    Duration.between(Instant.now(), date.toInstant())
                }.getOrNull()
        }
        ?.takeUnless(Duration::isNegative)

/**
 * Streams deserialized entities from an HTTP response body.
 *
 * @param serializer Used to deserialize entities in the body.
 * @param separatorBytes The byte sequence used to detect that a new element starts in an HTTP stream.
 * @param bufferSize The size of the buffer used to collect data for deserialization in bytes.
 * @param entityType The entity type this stream provides.
 * @param TEntity The entity type this stream provides.
 */
fun <TEntity : Any> Response.entitySequence(
    serializer: Serializer,
    separatorBytes: ByteString,
    bufferSize: Int,
    entityType: Class<TEntity>
): Sequence<TEntity> = sequence {
    val source = body!!.source()
    val buffer = Buffer()

    while(!finishedReading) {

    }

    while (source.read(buffer, bufferSize.toLong()) != -1L || !buffer.exhausted()) {
        var separatorIndex = buffer.indexOf(separatorBytes)
        do {
            val bytes = if (separatorIndex == -1L) buffer.readByteArray() else buffer.readByteArray(separatorIndex)
            buffer.skip(separatorBytes.size.toLong())

            parseEntity(serializer, bytes, entityType)?.let { yield(it) }
            separatorIndex = buffer.indexOf(separatorBytes)
        } while (separatorIndex != -1L)
    }

    if (!buffer.exhausted()) {
        parseEntity(serializer, buffer.readByteArray(), entityType)?.let { yield(it) }
    }
}

private fun <T : Any> parseEntity(bytes: ByteArray, serializer: Serializer, type: Class<T>): T? {
    if (bytes.isEmpty()) return null
    val mediaType: MediaType? = serializer.supportedMediaTypes.firstOrNull()
    return serializer.deserialize(bytes.toResponseBody(mediaType), type)
}
