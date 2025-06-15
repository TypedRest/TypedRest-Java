package net.typedrest.endpoints.raw

import net.typedrest.endpoints.*
import net.typedrest.http.uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.net.URI

/**
 * Implementation of an [UploadEndpoint] that accepts binary uploads using multi-part form encoding or raw bodies.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param formField The name of the form field to place the uploaded data into; null to use raw bodies instead of multi-part forms.
 */
class UploadEndpointImpl(
    private val referrer: Endpoint,
    private val relativeUri: URI,
    private val formField: String? = null
) : AbstractEndpoint(referrer, relativeUri), UploadEndpoint {
    /**
     * Creates a new upload endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param formField The name of the form field to place the uploaded data into; null to use raw bodies instead of multi-part forms.
     */
    constructor(referrer: Endpoint, relativeUri: String, formField: String? = null) :
        this(referrer, URI(relativeUri), formField)

    override fun uploadFrom(stream: InputStream, fileName: String?, mimeType: String?) {
        var body = stream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())

        if (formField != null) {
            val formDataBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(formField, fileName, body)

            body = formDataBuilder.build()
        }

        execute(Request.Builder().post(body).uri(uri).build()).close()
    }
}
