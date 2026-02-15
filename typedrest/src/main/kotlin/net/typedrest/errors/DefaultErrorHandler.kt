package net.typedrest.errors

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.typedrest.http.HttpStatusCode
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * Handles errors in HTTP responses by mapping status codes to common exception types.
 */
open class DefaultErrorHandler : ErrorHandler {
    @Throws(HttpException::class)
    override fun handle(response: Response) {
        if (response.isSuccessful) return

        val message = extractJsonMessage(response.body)
            ?: "${response.request.url} responded with ${response.code} ${response.message}"

        throw mapException(HttpStatusCode.parse(response.code) ?: HttpStatusCode.InternalServerError, message, response)
    }

    /**
     * Tries to extract an error message from the response body.
     *
     * @param body The response body.
     */
    protected open fun extractJsonMessage(body: ResponseBody): String? {
        val mediaType = body.contentType()?.toString()
        if (mediaType == null || (mediaType != "application/json" && !mediaType.endsWith("+json"))) {
            return null
        }

        return try {
            val decoded = Json.decodeFromString<JsonErrorResponse>(body.string())
            return decoded.message ?: decoded.details
        } catch (_: SerializationException) {
            null
        }
    }

    @Serializable
    private class JsonErrorResponse(val message: String?, val details: String? = null)

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
