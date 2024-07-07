package net.typedrest.endpoints.raw

import java.io.InputStream
import net.typedrest.endpoints.*
import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode
import java.io.*

/**
 * Endpoint for a binary blob that can be downloaded or uploaded.
 */
interface BlobEndpoint : Endpoint {
    /**
     * Queries the server about capabilities of the endpoint without performing any action.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun probe()

    /**
     * Indicates whether the server has specified that downloading is currently allowed.
     *
     * Uses cached data from the last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isDownloadAllowed: Boolean?

    /**
     * Downloads the blob's content to an input stream.
     *
     * @return An input stream with the blob's content.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun download(): InputStream

    /**
     * Downloads the blob's content to a file.
     *
     * @param path The path of the file to read the upload data from.
     * @throws IOException when the file at the specified path can not be written.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws HttpException for other non-success status codes.
     */
    fun download(path: String) =
        FileOutputStream(path).use { fileStream ->
            download().use { downloadStream ->
                downloadStream.copyTo(fileStream)
            }
        }

    /**
     * Indicates whether the server has specified that uploading is currently allowed.
     *
     * Uses cached data from the last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isUploadAllowed: Boolean?

    /**
     * Uploads data as the blob's content from an input stream.
     *
     * @param stream The input stream to read the upload data from.
     * @param mimeType The MIME type of the data to upload, nullable.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws HttpException for other non-success status codes.
     */
    fun uploadFrom(stream: InputStream, mimeType: String? = null)

    /**
     * Uploads content as the blob's content from a file.
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
            uploadFrom(fileStream, mimeType)
        }

    /**
     * Indicates whether the server has specified that deleting is currently allowed.
     *
     * Uses cached data from the last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isDeleteAllowed: Boolean?

    /**
     * Deletes the blob from the server.
     *
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun delete()
}
