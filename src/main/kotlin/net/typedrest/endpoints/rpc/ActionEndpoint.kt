package net.typedrest.endpoints.rpc

/**
 * RPC endpoint that is invoked with no input or output.
 */
class ActionEndpoint : RpcEndpointBase {
    /**
     * Creates a new action endpoint.
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the `referrer`'s. Add a `./` prefix here to imply a trailing slash in the `referrer`'s URI.
     */
    constructor(referrer: Endpoint, relativeUri: URI | string) {
        super(referrer, relativeUri)
    }

    /**
     * Invokes the action.
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link NotFoundError}: {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}
     * @throws {@link HttpError}: Other non-success status code
     */
    invoke() {
        this.send(HttpMethod.Post)
    }
}
