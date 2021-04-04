package net.typedrest.http

/**
 * Combines the results of multiple {@link LinkExtractor}s.
 */
class AggregateLinkExtractor : LinkExtractor {
    private val extractors: LinkExtractor[]

    /**
     * Creates a new aggregate link extractor.
     * @param extractors The link extractors to aggregate.
     */
    constructor(...extractors: LinkExtractor[]) {
        this.extractors = extractors
    }

    fun getLinks(response: Response) {
        let result: Link[] = []
        for (val extractor of this.extractors) {
            result = result.concat(extractor.getLinks(response))
        }
        return result
    }
}
