package net.typedrest.endpoints.raw

class UploadEndpoint : Endpoint {
    /**
     * Creates a new upload endpoint.
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the `referrer`'s. Add a `./` prefix here to imply a trailing slash in the `referrer`'s URI.
     * @param formField The name of the form field to place the uploaded data into leave unspecified to use raw bodies instead of a multi-part forms.
     */
    constructor(referrer: Endpoint, relativeUri: URI | string, private val formField?: string) {
        super(referrer, relativeUri)
        this.formField = formField
    }

    /**
     * Uploads data to the endpoint.
     * @param blob The blob to read the upload data from.
     * @param fileName The name of the uploaded file.
     * @throws {@link BadRequestError}: {@link HttpStatusCode.BadRequest}
     * @throws {@link AuthenticationError}: {@link HttpStatusCode.Unauthorized}
     * @throws {@link AuthorizationError}: {@link HttpStatusCode.Forbidden}
     * @throws {@link NotFoundError}: {@link HttpStatusCode.NotFound} or {@link HttpStatusCode.Gone}
     * @throws {@link HttpError}: Other non-success status code
     */
    fun upload(blob: Blob, fileName?: string) {
        if (this.formField) {
            val formData = FormData()
            formData.set(this.formField, blob, fileName)
            this.send(HttpMethod.Post, { [HttpHeader.ContentType]: "multipart/form-data" }, formData)
        } else {
            this.send(HttpMethod.Post, { [HttpHeader.ContentType]: blob.type }, blob)
        }
    }
}
