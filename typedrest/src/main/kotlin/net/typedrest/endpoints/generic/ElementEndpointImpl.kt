package net.typedrest.endpoints.generic

import net.typedrest.displayName
import net.typedrest.endpoints.*
import net.typedrest.errors.*
import net.typedrest.http.*
import net.typedrest.javaTypeOf
import okhttp3.*
import java.lang.reflect.Type
import java.net.URI

/**
 * Endpoint for an individual resource.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param entityType The type of entity the endpoint represents.
 * @param TEntity The type of entity the endpoint represents.
 */
open class ElementEndpointImpl<TEntity>(
    referrer: Endpoint,
    relativeUri: URI,
    protected val entityType: Type
) : AbstractCachingEndpoint(referrer, relativeUri), ElementEndpoint<TEntity> {
    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint represents.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Type) :
        this(referrer, URI(relativeUri), entityType)

    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param entityType The type of entity the endpoint represents.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Class<TEntity>) :
        this(referrer, relativeUri, entityType as Type)

    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint represents.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>) :
        this(referrer, URI(relativeUri), entityType as Type)

    override val response: TEntity?
        get() = responseCache?.getBody()?.let { deserialize(it, entityType) }

    override fun read(): TEntity =
        getContent()?.let { deserialize<TEntity>(it, entityType) }
            ?: throw IllegalStateException("Result not deserializable as ${entityType.displayName}")

    override fun exists(): Boolean =
        httpClient.newCall(Request.Builder().head().uri(uri).build()).execute().use { response ->
            when {
                response.isSuccessful -> true
                response.code == HttpStatusCode.NotFound.code || response.code == HttpStatusCode.Gone.code -> false
                else -> {
                    errorHandler.handle(response)
                    false
                }
            }
        }

    override val isSetAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.PUT)

    override fun set(entity: TEntity): TEntity? =
        tryReadAs(putContent(serialize(entity, entityType)))

    override val isMergeAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.PATCH)

    override fun merge(entity: TEntity): TEntity? {
        responseCache = null
        return tryReadAs(execute(Request.Builder().patch(serialize(entity, entityType)).uri(uri).build()))
    }

    override val isDeleteAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.DELETE)

    override fun delete() = deleteContent()

    override fun update(updateAction: (TEntity) -> TEntity, maxRetries: Int): TEntity? {
        var retryCounter = 0
        while (true) {
            val entity = updateAction(read())

            try {
                return set(entity)
            } catch (ex: ConflictException) {
                if (retryCounter++ >= maxRetries) throw ex
                ex.retryAfter?.let(Thread::sleep)
            }
        }
    }

    private fun tryReadAs(response: Response): TEntity? = response.use {
        if (it.code == HttpStatusCode.NoContent.code) return@use null

        try {
            deserialize<TEntity>(it.body, entityType)
        } catch (ex: Exception) {
            null
        }
    }
}

/**
 * Creates a new element endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TEntity The type of entity the endpoint represents. Can be a generic type such as `Page<Contact>`.
 */
inline fun <reified TEntity> ElementEndpointImpl(referrer: Endpoint, relativeUri: URI): ElementEndpointImpl<TEntity> =
    ElementEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())

/**
 * Creates a new element endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TEntity The type of entity the endpoint represents. Can be a generic type such as `Page<Contact>`.
 */
inline fun <reified TEntity> ElementEndpointImpl(referrer: Endpoint, relativeUri: String): ElementEndpointImpl<TEntity> =
    ElementEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())
