package net.typedrest.endpoints.rpc

import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * RPC endpoint that takes [TEntity] as input when invoked.
 *
 * @param TEntity The type of entity the endpoint takes as input.
 */
interface ConsumerEndpoint<in TEntity> : RpcEndpoint {
    /**
     * Sends the entity to the consumer.
     *
     * @param entity The entity to post as input.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun invoke(entity: TEntity)
}
