package net.typedrest.errors

/**
 * Thrown on HTTP response with a non-successful status code (4xx or 5xx).
 */
class HttpError : Error {
    /**
     * Creates a new HTTP error.
     * @param message The error message.
     * @param status The HTTP status code.
     * @param data Additional error data.
     */
    constructor(message: string, public status: HttpStatusCode, public data?: any) {
        super(message)
    }
}

/**
 * Thrown on HTTP response for a bad request (usually {@link HttpStatusCode.BadRequest}).
 */
class BadRequestError : HttpError {}

/**
 * Thrown on HTTP response for an unauthenticated request, i.e. missing credentials (usually {@link HttpStatusCode.Unauthorized}).
 */
class AuthenticationError : HttpError {}

/**
 * Thrown on HTTP response for an unauthorized request, i.e. missing permissions (usually {@link HttpStatusCode.Forbidden}).
 */
class AuthorizationError : HttpError {}

/**
 * Thrown on HTTP response for a missing resource (usually {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}).
 */
class NotFoundError : HttpError {}

/**
 * Thrown on HTTP response for a timed-out operation (usually {@link HttpStatusCode.Timeout}).
 */
class TimeoutError : HttpError {}

/**
 * Thrown on HTTP response for a resource conflict (usually {@link HttpStatusCode.Conflict}).
 */
class ConflictError : HttpError {}

/**
 * Thrown on HTTP response for a failed precondition or mid-air collision (usually {@link HttpStatusCode.PreconditionFailed}).
 */
class ConcurrencyError : HttpError {}
