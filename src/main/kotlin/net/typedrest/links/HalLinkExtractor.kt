package net.typedrest.http

/**
 * Extracts links from JSON bodies according to the Hypertext Application Language (HAL) specification.
 */
class HalLinkExtractor : LinkExtractor {
    fun getLinks(response: Response): Link[] {
        val contentType = response.headers.get(HttpHeader.ContentType)
        if (contentType?.startsWith("application/hal+json")) {
            return this.parseJsonBody(response.clone().json())
        } else {
            return []
        }
    }

    private fun parseJsonBody(body: any) {
        val links: Link[] = []

        val linkContainer: { [key: string]: any } = body._links
        if (linkContainer) {
            for (val rel in linkContainer) {
                if (Array.isArray(linkContainer[rel])) {
                    for (val link of linkContainer[rel]) {
                        links.push(this.parseLink(rel, link))
                    }
                } else {
                    links.push(this.parseLink(rel, linkContainer[rel]))
                }
            }
        }

        return links
    }

    private fun parseLink(rel: string, obj: any) {
        if (!obj.href)
            throw new Error("The link header is lacking the mandatory 'href' field.")
        return Link(rel, obj.href, obj.title, obj.templated ? true : false)
    }
}
