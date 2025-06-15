package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.errors.NotFoundException
import net.typedrest.http.uri
import okhttp3.Request
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
    private val entityType: Class<TEntity>,
    private val resultType: Class<TResult>
) : AbstractRpcEndpoint(referrer, relativeUri), FunctionEndpoint<TEntity, TResult> {
    /**
     * Creates a new function endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>, resultType: Class<TResult>) :
        this(referrer, URI(relativeUri), entityType, resultType)

    override fun invoke(entity: TEntity): TResult =
        execute(Request.Builder().post(serialize(entity, entityType)).uri(uri).build()).use { response ->
            response.body?.let { deserialize(it, resultType) }
                ?: throw NotFoundException("Result not deserializable as ${resultType.simpleName}")
        }
}
