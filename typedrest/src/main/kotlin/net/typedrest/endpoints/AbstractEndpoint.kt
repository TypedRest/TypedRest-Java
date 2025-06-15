package net.typedrest.endpoints

import com.damnhandy.uri.template.UriTemplate
import net.typedrest.errors.*
import net.typedrest.http.HttpMethod
import net.typedrest.join
import net.typedrest.links.*
import net.typedrest.serializers.Serializer
import okhttp3.*
import java.net.URI
import kotlin.collections.List

/**
 * Base class for building endpoints, i.e., remote HTTP resources.
 *
 * @param uri The HTTP URI of the remote element.
 * @param httpClient The HTTP client used to communicate with the remote element.
 * @param serializers A list of serializers used for entities received from the server, sorted from most to least preferred. Always uses first for sending to the server.
 * @param errorHandler Handles errors in HTTP responses.
 * @param linkExtractor Detects links in HTTP responses.
 */
abstract class AbstractEndpoint(
    override val uri: URI,
    override val httpClient: OkHttpClient,
    override val serializers: List<Serializer>,
    override val errorHandler: ErrorHandler,
    override val linkExtractor: LinkExtractor
) : Endpoint {
    /**
     * Creates a new endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     */
    constructor(referrer: Endpoint, relativeUri: URI) : this(
        referrer.uri.join(relativeUri),
        referrer.httpClient,
        referrer.serializers,
        referrer.errorHandler,
        referrer.linkExtractor
    )

    /**
     * Executes a request and handles various cross-cutting concerns regarding a response message such as discovering links and handling errors.
     *
     * @param request A callback that performs the actual HTTP request.
     * @return The HTTP response.
     */
    protected open fun execute(request: Request): Response {
        val response = httpClient.newCall(request).execute()

        links = linkExtractor.getLinks(response)
        handleCapabilities(response)
        errorHandler.handle(response)

        return response
    }

    /**
     * Handles various cross-cutting concerns regarding a response message such as discovering links and handling errors.
     *
     * @param response The HTTP response.
     * @return The HTTP response.
     */
    protected open fun handle(response: Response): Response {
        links = linkExtractor.getLinks(response)
        handleCapabilities(response)
        errorHandler.handle(response)

        return response
    }

    // NOTE: Always replace entire list rather than modifying it to ensure thread-safety.
    private var links: List<Link> = emptyList()

    // NOTE: Only modified during initial setup of the endpoint.
    private val defaultLinks: MutableMap<String, URI> = mutableMapOf()
    private val defaultLinkTemplates: MutableMap<String, UriTemplate> = mutableMapOf()

    /**
     * Registers one or more default links for a specific relation type.
     *
     * These links are used when no links with this relation type are provided by the server.
     * This should only be called during initial setup of the endpoint.
     *
     * @param rel The relation type of the link to add.
     * @param href The href of the link relative to this endpoint's URI. Use null to remove any previous entries for the relation type.
     */
    fun setDefaultLink(rel: String, href: String?) {
        if (href.isNullOrEmpty()) defaultLinks.remove(rel)
        else defaultLinks[rel] = uri.join(href)
    }

    /**
     * Registers a default link template for a specific relation type.
     *
     * This template is used when no template with this relation type is provided by the server.
     * This should only be called during initial setup of the endpoint.
     *
     * @param rel The relation type of the link template to add.
     * @param href The href of the link template relative to this endpoint's URI. Use null to remove any previous entry for the relation type.
     */
    fun setDefaultLinkTemplate(rel: String, href: String?) {
        if (href.isNullOrEmpty()) defaultLinkTemplates.remove(rel)
        else defaultLinkTemplates[rel] = UriTemplate.fromTemplate(href)
    }

    override fun getLinks(rel: String): List<Pair<URI, String?>> = links
        .filter { !it.templated && it.rel == rel }
        .map { uri.join(it.href) to it.title }
        .toList()
        .ifEmpty {
            defaultLinks[rel]?.let { listOf(it to null) } ?: listOf()
        }

    override fun link(rel: String): URI {
        val foundLinks = getLinks(rel)
        if (foundLinks.isEmpty()) {
            // Lazy lookup
            try {
                httpClient.newCall(Request.Builder().head().url(uri.toURL()).build()).execute()
            } catch (ex: Exception) {
                throw IllegalStateException("No link with rel=$rel provided by endpoint $uri.")
            }

            return getLinks(rel).firstOrNull()?.first
                ?: throw IllegalStateException("No link with rel=$rel provided by endpoint $uri.")
        }

        return foundLinks.first().first
    }

    override fun linkTemplate(rel: String, variables: Map<String, Any>): URI =
        uri.join(getLinkTemplate(rel).set(variables).expand())

    /**
     * Retrieves a link template with a specific relation type.
     *
     * @param rel The relation type of the link template to look for.
     * @return The unresolved link template.
     * @throws NotFoundException if no link template with the specified rel could be found.
     */
    fun getLinkTemplate(rel: String): UriTemplate = links
        .filter { it.templated && it.rel == rel }
        .map { UriTemplate.fromTemplate(it.href) }
        .firstOrNull()
        ?: defaultLinkTemplates[rel]
        // Lazy lookup
        ?: try {
            httpClient.newCall(Request.Builder().head().url(uri.toURL()).build()).execute()
            links
                .filter { it.templated && it.rel == rel }
                .map { UriTemplate.fromTemplate(it.href) }
                .firstOrNull()
                ?: throw NotFoundException("No link template with rel=$rel provided by endpoint $uri.")
        } catch (ex: Exception) {
            throw NotFoundException("No link template with rel=$rel provided by endpoint $uri.")
        }

    /**
     * Handles allowed HTTP methods and other capabilities reported by the server.
     */
    protected open fun handleCapabilities(response: Response) {
        val allowedMethodsHeader = response.headers("Allow")
        if (allowedMethodsHeader.isNotEmpty()) {
            allowedMethods = allowedMethodsHeader
                .flatMap { it.split(", ") }
                .mapNotNull { HttpMethod.parse(it) }
                .toSet()
        }
    }

    // NOTE: Always replace entire set rather than modifying it to ensure thread-safety.
    private var allowedMethods: Set<HttpMethod> = setOf()

    /**
     * Shows whether the server has indicated that a specific HTTP method is currently allowed.
     *
     * @param method The HTTP methods (e.g., GET, POST, ...) to check.
     * @return true if the method is allowed, false< if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods
     */
    protected fun isMethodAllowed(method: HttpMethod): Boolean? =
        if (allowedMethods.isEmpty()) null
        else allowedMethods.contains(method)

    /**
     * Serializes an entity using the first serializer.
     *
     * @param entity The entity to serialize.
     * @param T The type of entity to serialize.
     * @return The serialized entity as a request body.
     */
    fun <T> serialize(entity: T, type: Class<T>): RequestBody =
        serializers.first().serialize(entity, type)

    /**
     * Serializes a list of entities using the first serializer.
     *
     * @param entities The entities to serialize.
     * @param T The type of entity to serialize.
     * @return The serialized entity as a request body.
     */
    fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody =
        serializers.first().serializeList(entities, type)

    /**
     * Deserializes an entity using the first serializer that supports the body's content type.
     *
     * @param body The request body to deserialize into an entity.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to deserialize.
     * @return The deserialized response body as an entity. null if the body could not be deserialized.
     */
    fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        getSerializer(body).deserialize(body, type)

    /**
     * Deserializes a list of entities using the first serializer that supports the body's content type.
     *
     * @param body The request body to deserialize into an entity.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to deserialize.
     * @return The deserialized response body as a list of entities. null if the body could not be deserialized.
     */
    fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        getSerializer(body).deserializeList(body, type)

    private fun getSerializer(body: ResponseBody): Serializer {
        val mediaType = body.contentType() ?: throw IllegalArgumentException("Response body has no media type")
        return serializers.find { it.supportedMediaTypes.contains(mediaType) }
            ?: throw IllegalArgumentException("No serializer found for media type: $mediaType")
    }
}
