package net.typedrest.endpoints

import net.typedrest.errors.ErrorHandler
import net.typedrest.errors.NotFoundException
import net.typedrest.links.LinkExtractor
import net.typedrest.serializers.Serializer
import okhttp3.OkHttpClient
import java.net.URI
import kotlin.collections.List

/**
 * Represents an endpoint, i.e., a remote HTTP resource.
 */
interface Endpoint {
    /**
     * The HTTP URI of the remote resource.
     */
    val uri: URI

    /**
     * The HTTP client used to communicate with the remote resource.
     */
    val httpClient: OkHttpClient

    /**
     * A list of serializers used for entities received from the server,
     * sorted from most to least preferred. Always uses first for sending to the server.
     */
    val serializers: List<Serializer>

    /**
     * Handles errors in responses.
     */
    val errorHandler: ErrorHandler

    /**
     * Extracts links from responses.
     */
    val linkExtractor: LinkExtractor

    /**
     * Resolves all links with a specific relation type. Uses cached data from last response.
     *
     * @param rel The relation type of the links to look for.
     * @return A list of pairs of URI and optional title.
     */
    fun getLinks(rel: String): List<Pair<URI, String?>>

    /**
     * Resolves a single link with a specific relation type. Uses cached data from last response if possible.
     *
     * Tries lazy lookup with HTTP HEAD on cache miss.
     *
     * @param rel The relation type of the link to look for.
     * @throws NotFoundException if no link with the specified relation could be found.
     * @return The URI of the link.
     */
    @Throws(NotFoundException::class)
    fun link(rel: String): URI

    /**
     * Resolves a link template with a specific relation type. Uses cached data from last response if possible.
     *
     * Tries lazy lookup with HTTP HEAD on cache miss.
     *
     * @param rel The relation type of the link template to look for.
     * @param variables Variables for resolving the template.
     * @throws NotFoundException if no link template with the specified relation could be found.
     * @return The href of the link resolved relative to this endpoint's URI.
     */
    @Throws(NotFoundException::class)
    fun linkTemplate(rel: String, variables: Map<String, Any>): URI
}
