package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.http.uri
import net.typedrest.javaTypeOf
import okhttp3.Request
import java.lang.reflect.Type
import java.net.URI

/**
 * RPC endpoint that takes [TEntity] as input when invoked.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param entityType The type of entity the endpoint takes as input.
 * @param TEntity The type of entity the endpoint takes as input.
 */
open class ConsumerEndpointImpl<TEntity>(
    referrer: Endpoint,
    relativeUri: URI,
    private val entityType: Type
) : AbstractRpcEndpoint(referrer, relativeUri), ConsumerEndpoint<TEntity> {
    /**
     * Creates a new consumer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Type) :
        this(referrer, URI(relativeUri), entityType)

    /**
     * Creates a new consumer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param entityType The type of entity the endpoint takes as input.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Class<TEntity>) :
        this(referrer, relativeUri, entityType as Type)

    /**
     * Creates a new consumer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>) :
        this(referrer, URI(relativeUri), entityType as Type)

    override fun invoke(entity: TEntity) =
        execute(Request.Builder().post(serialize(entity, entityType)).uri(uri).build()).close()
}

/**
 * Creates a new consumer endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TEntity The type of entity the endpoint takes as input. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TEntity> ConsumerEndpointImpl(referrer: Endpoint, relativeUri: URI): ConsumerEndpointImpl<TEntity> =
    ConsumerEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())

/**
 * Creates a new consumer endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TEntity The type of entity the endpoint takes as input. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TEntity> ConsumerEndpointImpl(referrer: Endpoint, relativeUri: String): ConsumerEndpointImpl<TEntity> =
    ConsumerEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())
