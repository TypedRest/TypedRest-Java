package net.typedrest.endpoints.raw

import net.typedrest.endpoints.Endpoint
import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode
import java.io.*
import kotlin.io.path.Path
import kotlin.io.path.name

/**
 * Endpoint that accepts binary uploads.
 */
interface UploadEndpoint : Endpoint {
    /**
     * Uploads data to the endpoint from a stream.
     *
     * @param stream The input stream to read the upload data from.
     * @param fileName The name of the uploaded file.
     * @param mimeType The MIME type of the data to upload, nullable.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws HttpException for other non-success status codes.
     */
    fun uploadFrom(stream: InputStream, fileName: String? = null, mimeType: String? = null)

    /**
     * Uploads data to the endpoint from a file.
     *
     * @param path The path of the file to read the upload data from.
     * @param mimeType The MIME type of the data to upload, nullable.
     * @throws IOException when the file at the specified path can not be read.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws HttpException for other non-success status codes.
     */
    fun uploadFrom(path: String, mimeType: String? = null) =
        FileInputStream(path).use { fileStream ->
            uploadFrom(fileStream, Path(path).name, mimeType)
        }
}
