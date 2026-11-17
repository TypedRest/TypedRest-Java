package net.typedrest.endpoints.rpc

import net.typedrest.displayName
import net.typedrest.endpoints.*
import net.typedrest.http.uri
import net.typedrest.javaTypeOf
import okhttp3.Request
import java.lang.reflect.Type
import java.net.URI

/**
 * RPC endpoint that takes [TEntity] as input and returns [TResult] as output when invoked.
 *
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param entityType The type of entity the endpoint takes as input.
 * @param resultType The type of entity the endpoint returns as output.
 * @param TEntity The type of entity the endpoint takes as input.
 * @param TResult The type of entity the endpoint returns as output.
 */
open class FunctionEndpointImpl<TEntity, TResult>(
    referrer: Endpoint,
    relativeUri: URI,
    private val entityType: Type,
    private val resultType: Type
) : AbstractRpcEndpoint(referrer, relativeUri), FunctionEndpoint<TEntity, TResult> {
    /**
     * Creates a new function endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Type, resultType: Type) :
        this(referrer, URI(relativeUri), entityType, resultType)

    /**
     * Creates a new function endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Class<TEntity>, resultType: Class<TResult>) :
        this(referrer, relativeUri, entityType as Type, resultType as Type)

    /**
     * Creates a new function endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>, resultType: Class<TResult>) :
        this(referrer, URI(relativeUri), entityType as Type, resultType as Type)

    override fun invoke(entity: TEntity): TResult =
        execute(Request.Builder().post(serialize(entity, entityType)).uri(uri).build()).use { response ->
            deserialize<TResult>(response.body, resultType)
                ?: throw IllegalStateException("Result not deserializable as ${resultType.displayName}")
        }
}

/**
 * Creates a new function endpoint, taking the entity and result types from the reified type arguments.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TEntity The type of entity the endpoint takes as input. Can be a generic type such as `List<Contact>`.
 * @param TResult The type of entity the endpoint returns as output. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TEntity, reified TResult> FunctionEndpointImpl(referrer: Endpoint, relativeUri: URI): FunctionEndpointImpl<TEntity, TResult> =
    FunctionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>(), javaTypeOf<TResult>())

/**
 * Creates a new function endpoint, taking the entity and result types from the reified type arguments.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TEntity The type of entity the endpoint takes as input. Can be a generic type such as `List<Contact>`.
 * @param TResult The type of entity the endpoint returns as output. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TEntity, reified TResult> FunctionEndpointImpl(referrer: Endpoint, relativeUri: String): FunctionEndpointImpl<TEntity, TResult> =
    FunctionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>(), javaTypeOf<TResult>())
