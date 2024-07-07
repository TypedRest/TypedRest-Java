package net.typedrest.errors

import okhttp3.Response

/**
 * Handles errors in HTTP responses.
 */
interface ErrorHandler {
    /**
     * Throws appropriate `Exception`s based on HTTP status codes and response bodies.
     *
     * @throws HttpException
     */
    fun handle(response: Response)
}
