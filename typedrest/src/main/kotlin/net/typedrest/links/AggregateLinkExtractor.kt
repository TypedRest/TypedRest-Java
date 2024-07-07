package net.typedrest.links

import okhttp3.Response

/**
 * Combines the results of multiple [LinkExtractor]s.
 */
class AggregateLinkExtractor(private vararg val extractors: LinkExtractor) : LinkExtractor {
    override fun getLinks(response: Response): List<Link> =
        extractors.flatMap { extractor -> extractor.getLinks(response) }
}
