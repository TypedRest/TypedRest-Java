package net.typedrest.endpoints.rpc

import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * RPC endpoint that takes [TEntity] as input and returns [TResult] as output when invoked.
 *
 * @param TEntity The type of entity the endpoint takes as input.
 * @param TResult The type of entity the endpoint returns as output.
 */
interface FunctionEndpoint<in TEntity, out TResult> : RpcEndpoint {
    /**
     * RpcEndpoint
     *
     * @param entity The entity to post as input.
     * @return The result returned by the server.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun invoke(entity: TEntity): TResult
}
