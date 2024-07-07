package net.typedrest.endpoints.rpc

import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * RPC endpoint that is invoked with no input or output.
 */
interface ActionEndpoint : RpcEndpoint {
    /**
     * Invokes the action.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun invoke()
}
