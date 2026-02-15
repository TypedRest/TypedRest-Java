package net.typedrest.endpoints.generic

import net.typedrest.endpoints.*
import net.typedrest.errors.NotFoundException
import net.typedrest.http.*
import okhttp3.*
import java.net.URI
import java.net.URLEncoder

/**
 * Endpoint for a collection of [TEntity]s addressable as [TElementEndpoint]s.
 *
 * Use the more constrained [CollectionEndpointImpl] when possible.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of individual elements in the collection.
 * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
 * @param TEntity The type of individual elements in the collection.
 * @param TElementEndpoint The type of [ElementEndpoint] to provide for individual [TEntity]s.
 */
open class GenericCollectionEndpointImpl<TEntity, TElementEndpoint : ElementEndpoint<TEntity>>(
    referrer: Endpoint,
    relativeUri: URI,
    private val entityType: Class<TEntity>,
    private val elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint
) : AbstractCachingEndpoint(referrer, relativeUri), GenericCollectionEndpoint<TEntity, TElementEndpoint> {
    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the collection.
     * @param elementEndpointFactory The factory for constructing [TElementEndpoint]s to provide for individual elements.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>, elementEndpointFactory: (referrer: Endpoint, relativeUri: URI) -> TElementEndpoint) :
        this(referrer, URI(relativeUri), entityType, elementEndpointFactory)

    init {
        setDefaultLinkTemplate("child", "./{id}")
    }

    operator fun get(id: String): TElementEndpoint {
        return elementEndpointFactory(this, linkTemplate("child", mapOf("id" to URLEncoder.encode(id, "UTF-8"))))
    }

    override operator fun get(entity: TEntity): TElementEndpoint =
        get(
            tryGetId(entity) ?: throw IllegalStateException("${entityType.simpleName} has no property named id.")
        )

    override val readAllAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.GET)

    override fun readAll(): List<TEntity> =
        getContent()?.let { deserializeList(it, entityType) }
            ?: throw NotFoundException("Result not deserializable as List<${entityType.simpleName}>")

    /**
     * The value used for [HttpContentRangeHeader.unit].
     */
    var rangeUnit: String = "elements"

    override fun handleCapabilities(response: Response) {
        super.handleCapabilities(response)
        readRangeAllowed = response.headers("Accept-Ranges").contains(rangeUnit)
    }

    override var readRangeAllowed: Boolean? = null

    override fun readRange(from: Long?, to: Long?): PartialResponse<TEntity> =
        execute(Request.Builder().get().uri(uri).header("Range", "${rangeUnit}=${from ?: ""}-${to ?: ""}").build())
            .use { response ->
                PartialResponse(
                    deserializeList(response.body, entityType) ?: throw NotFoundException("Result not deserializable as List<${entityType.simpleName}>"),
                    HttpContentRangeHeader.parse(response.headers)
                )
            }

    override val createAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.POST)

    override fun create(entity: TEntity): TElementEndpoint? {
        val response = execute(Request.Builder().post(serialize(entity, entityType)).uri(uri).build())
        val responseCache = ResponseCache.from(response)

        val location = response.header("Location")
        val elementEndpoint = if (location != null) {
            // Explicit element endpoint URL from "Location" header
            elementEndpointFactory(this, URI(location))
        } else {
            // Infer URL from entity ID in response body
            responseCache
                ?.getBody()
                ?.let { deserialize(it, entityType) }
                ?.let(::tryGetId)
                ?.let(::get)
        }

        if (elementEndpoint is CachingEndpoint) {
            elementEndpoint.responseCache = responseCache
        }

        return elementEndpoint
    }

    private fun tryGetId(entity: TEntity) =
        entityType.methods.firstOrNull { it.name.lowercase() == "getid" }
            ?.invoke(entity)?.toString()

    override val createAllAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.PATCH)

    override fun createAll(entities: Iterable<TEntity>) =
        execute(Request.Builder().patch(serializeList(entities, entityType)).uri(uri).build()).close()

    override val setAllAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.PUT)

    override fun setAll(entities: Iterable<TEntity>) {
        putContent(serializeList(entities, entityType))
    }
}
