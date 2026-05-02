package net.typedrest.errors

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.typedrest.http.HttpStatusCode
import net.typedrest.http.isJson
import okhttp3.Response

/**
 * Handles errors in HTTP responses by mapping status codes to common exception types.
 */
open class DefaultErrorHandler : ErrorHandler {
    @Throws(HttpException::class)
    override fun handle(response: Response) {
        if (response.isSuccessful) return

        val message = extractJsonMessage(response)
            ?: "${response.request.url} responded with ${response.code} ${response.message}"

        throw mapException(HttpStatusCode.parse(response.code) ?: HttpStatusCode.InternalServerError, message, response)
    }

    /**
     * Tries to extract an error message from the response body.
     *
     * @param response The HTTP response.
     */
    protected open fun extractJsonMessage(response: Response): String? {
        if (response.body.contentType()?.isJson != true) return null

        return try {
            val root = Json.parseToJsonElement(response.peekBody(Long.MAX_VALUE).string()) as? JsonObject ?: return null
            (root["message"] ?: root["details"])?.jsonPrimitive?.contentOrNull
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    /**
     * Maps the HTTP status code to an exception.
     *
     * @param status The status code.
     * @param message An error message to include in the exception.
     * @param response The HTTP response to include in the exception.
     */
    protected open fun mapException(status: HttpStatusCode, message: String, response: Response?) =
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
