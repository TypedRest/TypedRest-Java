package net.typedrest.links

import okhttp3.Response

/**
 * Extracts links from responses.
 */
interface LinkExtractor {
    /**
     * Extracts links from the `response`.
     */
    fun getLinks(response: Response): List<Link>
}
