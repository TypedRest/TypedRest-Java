package net.typedrest.errors

import net.typedrest.http.HttpStatusCode
import okhttp3.Response
import java.time.Duration

/**
 * Thrown on HTTP response with a non-successful status code (4xx or 5xx).
 * @param message The error message.
 * @param status The HTTP status code.
 * @param response The full HTTP response.
 */
open class HttpException(message: String, val status: HttpStatusCode, val response: Response? = null) : Exception(message) {
    /**
     * The wait time before retrying suggested by the server.
     */
    val retryAfter: Duration? = response
        ?.header("Retry-After")
        ?.toLongOrNull()
        ?.let(Duration::ofSeconds)
}

/**
 * Thrown on HTTP response for a bad request (usually [HttpStatusCode.BadRequest]).
 */
open class BadRequestException(message: String = "Bad request", status: HttpStatusCode = HttpStatusCode.BadRequest, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for an unauthenticated request, i.e. missing credentials (usually [HttpStatusCode.Unauthorized]).
 */
open class AuthenticationException(message: String = "Unauthorized", status: HttpStatusCode = HttpStatusCode.Unauthorized, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for an unauthorized request, i.e. missing permissions (usually [HttpStatusCode.Forbidden]).
 */
open class AuthorizationException(message: String = "Forbidden", status: HttpStatusCode = HttpStatusCode.Forbidden, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for a missing resource (usually [HttpStatusCode.NotFound] or [HttpStatusCode.Gone]).
 */
open class NotFoundException(message: String = "Not found", status: HttpStatusCode = HttpStatusCode.NotFound, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for a timed-out operation (usually [HttpStatusCode.RequestTimeout]).
 */
open class TimeoutException(message: String = "Timeout", status: HttpStatusCode = HttpStatusCode.RequestTimeout, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for a resource conflict (usually [HttpStatusCode.Conflict]).
 */
open class ConflictException(message: String = "Conflict", status: HttpStatusCode = HttpStatusCode.Conflict, response: Response? = null) : HttpException(message, status, response)

/**
 * Thrown on HTTP response for a failed precondition or mid-air collision (usually [HttpStatusCode.PreconditionFailed]).
 */
open class ConcurrencyException(message: String = "Precondition failed", status: HttpStatusCode = HttpStatusCode.PreconditionFailed, response: Response? = null) : HttpException(message, status, response)
