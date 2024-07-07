package net.typedrest.endpoints.rpc

import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * RPC endpoint that takes no input and returns [TResult] as output when invoked.
 *
 * @param TResult The type of entity the endpoint returns as output.
 */
interface ProducerEndpoint<out TResult> : RpcEndpoint {
    /**
     * Gets a result from the producer.
     *
     * @return The result returned by the server.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun invoke(): TResult
}
