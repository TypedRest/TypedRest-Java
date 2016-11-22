package com.oneandone.typedrest;

import java.io.*;
import java.util.Optional;
import org.apache.http.*;
import org.apache.http.entity.ContentType;

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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void probe()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that {@link #download()} is
     * currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the method is allowed. If no request has
     * been sent yet or the server did not specify allowed methods
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isDownloadAllowed();

    /**
     * Downloads the blob's content.
     *
     * @return A stream with the blob's content.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    InputStream download()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that
     * {@link #upload(java.io.InputStream, org.apache.http.entity.ContentType)}
     * is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the method is allowed. If no request has
     * been sent yet or the server did not specify allowed methods
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isUploadAllowed();

    /**
     * Uploads new content for the blob.
     *
     * @param stream A stream with the blob's new content.
     * @param contentType The MIME type of the file to upload.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void upload(InputStream stream, ContentType contentType)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException;

    /**
     * Uploads a byte array as the blob's content.
     *
     * @param data The byte array holding the data to upload.
     * @param contentType The MIME type of the file to upload.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void upload(byte[] data, ContentType contentType)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException;

    /**
     * Uploads a local file as the blob's content.
     *
     * @param file The local file to read the data from.
     * @param contentType The MIME type of the file to upload.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void upload(File file, ContentType contentType)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that {@link #delete()} is
     * currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the method is allowed. If no request has
     * been sent yet or the server did not specify allowed methods
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isDeleteAllowed();

    /**
     * Deletes the blob from the server.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void delete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException;

}
