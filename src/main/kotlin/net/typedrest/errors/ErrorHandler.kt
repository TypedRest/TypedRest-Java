package net.typedrest.errors

import okhttp3.Response

/**
 * Handles errors in HTTP responses.
 */
interface ErrorHandler {
    /**
     * Throws appropriate `Error`s based on HTTP status codes and response bodies.
     * @throws {@link HttpError}
     */
    fun handle(response: Response)
}
