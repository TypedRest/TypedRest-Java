package com.oneandone.typedrest;

import java.io.*;
import java.util.Optional;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * REST endpoint that represents a single binary blob that can downloaded and
 * uploaded.
 */
public interface BlobEndpoint extends Endpoint {

    /**
     * Queries the server about capabilities of the endpoint without performing
     * any action.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void probe()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;

    /**
     * Shows whether the server has indicated that
     * {@link #downloadTo(java.io.OutputStream)} is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isDownloadAllowed();

    /**
     * Downloads the blob's content.
     *
     * @param stream The stream to write the downloaded data to.
     * @return The MIME type of the downloaded blob.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    String downloadTo(OutputStream stream)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;

    /**
     * Shows whether the server has indicated that
     * {@link #uploadFrom(java.io.File, java.lang.String)} is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isUploadAllowed();

    /**
     * Uploads a local file as the blob's content.
     *
     * @param file The local file to read the data from.
     * @param mimeType The MIME type of the file to upload.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void uploadFrom(File file, String mimeType)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;
}
