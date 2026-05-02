package net.typedrest.links

import kotlinx.serialization.*
import kotlinx.serialization.json.*
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
                .links.flatMap { (rel, element) -> toLinks(rel, element) }
        } catch (_: SerializationException) {
            emptyList()
        }

    private fun toLinks(rel: String, element: JsonElement): List<Link> =
        when (element) {
            is JsonObject -> listOfNotNull(toLink(rel, element))
            is JsonArray -> element.mapNotNull { (it as? JsonObject)?.let { obj -> toLink(rel, obj) } }
            else -> emptyList()
        }

    private fun toLink(rel: String, obj: JsonObject): Link? =
        try {
            val halLink = Json.decodeFromJsonElement<HalLink>(obj)
            Link(rel, halLink.href, halLink.title, halLink.templated)
        } catch (_: SerializationException) {
            null
        }

    @Serializable
    private class HalLinksContainer(
        @SerialName("_links") val links: Map<String, JsonElement>
    )

    @Serializable
    private class HalLink(
        val href: String,
        val title: String? = null,
        val templated: Boolean = false
    )
}
