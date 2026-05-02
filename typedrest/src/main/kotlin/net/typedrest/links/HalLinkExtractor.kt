package net.typedrest.links

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.Response

/**
 * Extracts links from JSON bodies according to the Hypertext Application Language (HAL) specification.
 */
class HalLinkExtractor : LinkExtractor {
    override fun getLinks(response: Response): List<Link> {
        val mediaType = response.body.contentType() ?: return emptyList()
        if ("${mediaType.type}/${mediaType.subtype}" != "application/hal+json") return emptyList()
        return parseJsonBody(response.peekBody(Long.MAX_VALUE).string())
    }

    private fun parseJsonBody(body: String): List<Link> =
        try {
            Json.decodeFromString<HalLinksContainer>(body)
                .links.map { (rel, halLink) ->
                    Link(rel, halLink.href, halLink.title, halLink.templated)
                }
        } catch (_: SerializationException) {
            emptyList()
        }

    @Serializable
    private class HalLinksContainer(
        @SerialName("_links") val links: Map<String, HalLink>
    )

    @Serializable
    private class HalLink(
        val href: String,
        val title: String? = null,
        val templated: Boolean = false
    )
}
