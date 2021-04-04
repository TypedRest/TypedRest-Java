package net.typedrest.http

/**
 * Extracts links from responses.
 */
interface LinkExtractor {
    /**
     * Extracts links from the `response`.
     */
    fun getLinks(response: Response): Link[]
}
