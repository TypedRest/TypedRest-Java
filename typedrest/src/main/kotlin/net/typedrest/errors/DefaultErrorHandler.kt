package net.typedrest.errors

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.typedrest.http.HttpStatusCode
import okhttp3.Response
import java.io.IOException

/**
 * Handles errors in HTTP responses by mapping status codes to common exception types.
 */
class DefaultErrorHandler : ErrorHandler {
    @Throws(HttpException::class)
    override fun handle(response: Response) {
        if (response.isSuccessful) return

        val body = response.body?.string()
        val message = extractJsonMessage(response, body)
            ?: "${response.request.url} responded with ${response.code} ${response.message}"

        throw mapException(HttpStatusCode.parse(response.code) ?: HttpStatusCode.InternalServerError, message, response)
    }

    private fun extractJsonMessage(response: Response, body: String?): String? {
        if (body.isNullOrEmpty()) return null

        val mediaType = response.body?.contentType()?.toString()
        if (mediaType != "application/json" && (mediaType == null || !mediaType.endsWith("+json"))) return null

        return try {
            val decoded = Json.decodeFromString<JsonErrorResponse>(body)
            return decoded.message ?: decoded.details
        } catch (e: SerializationException) {
            null
        }
    }

    @Serializable
    private class JsonErrorResponse(val message: String?, val details: String? = null)

    private fun mapException(status: HttpStatusCode, message: String, response: Response?) =
        when (status) {
            HttpStatusCode.BadRequest -> BadRequestException(message, status, response)
            HttpStatusCode.Unauthorized -> AuthenticationException(message, status, response)
            HttpStatusCode.Forbidden -> AuthorizationException(message, status, response)
            HttpStatusCode.NotFound, HttpStatusCode.Gone -> NotFoundException(message, status, response)
            HttpStatusCode.Conflict, HttpStatusCode.PreconditionFailed, HttpStatusCode.RangeNotSatisfiable -> ConflictException(message, status, response)
            HttpStatusCode.RequestTimeout -> TimeoutException(message, status, response)
            else -> HttpException(message, status, response)
        }
}
