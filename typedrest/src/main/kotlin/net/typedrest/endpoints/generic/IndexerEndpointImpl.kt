package net.typedrest.endpoints.generic

import net.typedrest.endpoints.Endpoint
import net.typedrest.endpoints.AbstractEndpoint
import java.net.URI
import java.net.URLEncoder

/**
 * Endpoint that addresses child [TElementEndpoint]s by ID.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
 * @param TElementEndpoint The type of [Endpoint] to provide for individual elements.
 */
open class IndexerEndpointImpl<TElementEndpoint : Endpoint>(
    referrer: Endpoint,
    relativeUri: URI,
    private val elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint
) : AbstractEndpoint(referrer, relativeUri), IndexerEndpoint<TElementEndpoint> {
    /**
     * Creates a new indexer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
     */
    constructor(referrer: Endpoint, relativeUri: String, elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint) :
        this(referrer, URI(relativeUri), elementEndpointFactory)

    init {
        setDefaultLinkTemplate(rel = "child", href = "./{id}")
    }

    override operator fun get(id: String): TElementEndpoint =
        if (id.isNotEmpty()) elementEndpointFactory(this, linkTemplate("child", mapOf("id" to URLEncoder.encode(id, "UTF-8"))))
        else throw IllegalArgumentException("ID must not be null or empty")
}
