package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.http.uri
import okhttp3.Request
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
    private val entityType: Class<TEntity>
) : AbstractRpcEndpoint(referrer, relativeUri), ConsumerEndpoint<TEntity> {
    /**
     * Creates a new consumer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of entity the endpoint takes as input.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>) :
        this(referrer, URI(relativeUri), entityType)

    override fun invoke(entity: TEntity) =
        execute(Request.Builder().post(serialize(entity, entityType)).uri(uri).build()).close()
}
