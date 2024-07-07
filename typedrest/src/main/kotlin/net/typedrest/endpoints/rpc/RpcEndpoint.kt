package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.Endpoint
import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * An endpoint for a non-RESTful resource that acts like a callable function.
 */
interface RpcEndpoint : Endpoint {
    /**
     * Queries the server about capabilities of the endpoint without performing any action.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun probe()

    /**
     * Indicates whether the server has specified the invoke method is currently allowed.
     *
     * Uses cached data from the last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isInvokeAllowed: Boolean?
}
