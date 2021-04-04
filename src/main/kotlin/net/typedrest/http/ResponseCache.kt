package net.typedrest.http

/**
 * Caches the contents of a `Response`.
 */
class ResponseCache {
    /**
     * Creates a new cache.
     * @param content The content of the `Response`.
     * @param contentType The MIME type of the `content`.
     * @param eTag The E-Tag associated with the `content`.
     */
    constructor(public val content: string, public val contentType?: string, public val eTag?: string) {
    }

    /**
     * Creates a new cache from a `Response`.
     */
    static fun from(response: Response) {
        return ResponseCache(
            response.text(),
            response.headers.get(HttpHeader.ContentType) ?? undefined,
            response.headers.get(HttpHeader.ETag) ?? undefined)
    }
}
