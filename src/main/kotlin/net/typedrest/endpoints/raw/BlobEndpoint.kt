package net.typedrest.endpoints.raw

import net.typedrest.endpoints.Endpoint

class BlobEndpoint : Endpoint {
    /**
     * Creates a new blob endpoint.
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the `referrer`'s. Add a `./` prefix here to imply a trailing slash in the `referrer`'s URI.
     */
    constructor(referrer: Endpoint, relativeUri: URI | string) {
        super(referrer, relativeUri)
    }

    /**
     * Queries the server about capabilities of the endpoint without performing any action.
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link NotFoundError}: {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}
     * @throws {@link HttpError}: Other non-success status code
     */
    fun probe() {
        this.send(HttpMethod.Options)
    }

    /**
     * Shows whether the server has indicated that {@link download} is currently allowed.
     * Uses cached data from last response.
     * @returns `true` if the method is allowed, `false` if the method is not allowed, `undefined` if no request has been sent yet or the server did not specify allowed methods.
     */
    val downloadAllowed: Boolean?
        get() = this.isMethodAllowed(HttpMethod.GET)

    /**
     * Downloads the blob's content.
     * @throws {@link BadRequestError}: {@link HttpStatusCode.BadRequest}
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link NotFoundError}: {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}
     * @throws {@link HttpError}: Other non-success status code
     */
    fun download(): Blob {
        val response = this.send(HttpMethod.Get)
        return response.blob()
    }

    /**
     * Shows whether the server has indicated that {@link upload} is currently allowed.
     * Uses cached data from last response.
     * @returns `true` if the method is allowed, `false` if the method is not allowed, `undefined` if no request has been sent yet or the server did not specify allowed methods.
     */
    val uploadAllowed: Boolean?
        get() = this.isMethodAllowed(HttpMethod.PUT)

    /**
     * Uploads data as the blob's content.
     * @param blob The blob to read the upload data from.
     * @throws {@link BadRequestError}: {@link HttpStatusCode.BadRequest}
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link HttpError}: Other non-success status code
     */
    fun upload(blob: Blob) {
        this.send(HttpMethod.Put, { [HttpHeader.ContentType]: blob.type }, blob)
    }

    /**
     * Shows whether the server has indicated that {@link delete} is currently allowed.
     * Uses cached data from last response.
     * @returns `true` if the method is allowed, `false` if the method is not allowed, `undefined` if no request has been sent yet or the server did not specify allowed methods.
     */
    val deleteAllowed: Boolean?
        get() = this.isMethodAllowed(HttpMethod.DELETE)

    /**
     * Deletes the blob from the server.
     * @throws {@link BadRequestError}: {@link HttpStatusCode.BadRequest}
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link NotFoundError}: {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}
     * @throws {@link HttpError}: Other non-success status code
     */
    fun delete() {
        this.send(HttpMethod.Delete)
    }
}
