package net.typedrest.endpoints.generic

import net.typedrest.endpoints.*
import net.typedrest.errors.*
import net.typedrest.http.*
import okhttp3.*
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
    private val entityType: Class<TEntity>
) : AbstractCachingEndpoint(referrer, relativeUri), ElementEndpoint<TEntity> {
    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint represents.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>) :
        this(referrer, URI(relativeUri), entityType)

    override val response: TEntity?
        get() = responseCache?.getBody()?.let { deserialize(it, entityType) }

    override fun read(): TEntity =
        getContent()?.let { deserialize(it, entityType) }
            ?: throw NotFoundException("Result not deserializable as ${entityType.simpleName}")

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
            } catch (ex: HttpException) {
                if (retryCounter++ >= maxRetries) throw ex
                ex.retryAfter?.let(Thread::sleep)
            }
        }
    }

    private fun tryReadAs(response: Response): TEntity? {
        if (response.code == HttpStatusCode.NoContent.code) {
            return null
        }

        return try {
            deserialize(response.body, entityType)
        } catch (ex: Exception) {
            null
        }
    }
}
