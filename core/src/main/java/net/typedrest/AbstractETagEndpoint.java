package net.typedrest;

import java.io.*;
import java.net.*;
import lombok.*;
import org.apache.http.*;
import static org.apache.http.HttpHeaders.IF_NONE_MATCH;
import static org.apache.http.HttpStatus.SC_NOT_MODIFIED;
import org.apache.http.client.fluent.*;

/**
 * Base class for building REST endpoints that use ETags (entity tags) for
 * caching and to avoid lost updates.
 */
public class AbstractETagEndpoint
        extends AbstractEndpoint {

    protected AbstractETagEndpoint(Endpoint referrer, URI relativeUri) {
        super(referrer, relativeUri);
    }

    protected AbstractETagEndpoint(Endpoint referrer, String relativeUri) {
        super(referrer, relativeUri);
    }

    @AllArgsConstructor
    private class Memory {

        public final String etag;
        public final HttpEntity content;
    }

    // NOTE: Replace entire object rather than modifying it to ensure thread-safety.
    private Memory last;

    /**
     * Performs an HTTP GET request on the {@link Endpoint#getUri()} and caches
     * the response if the server sends an ETag header.
     *
     * Sends {@link HttpHeaders#IF_NONE_MATCH} if there is already a cached
     * ETag.
     *
     * @return The response of the request or the cached response if the server
     * responded with {@link HttpStatus#SC_NOT_MODIFIED}.
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
    protected HttpEntity getContent()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        Request request = Request.Get(uri);
        if (last != null) {
            request = request.addHeader(IF_NONE_MATCH, last.etag);
        }

        HttpResponse response = execute(request);
        if (response.getStatusLine().getStatusCode() == SC_NOT_MODIFIED && last != null) {
            return last.content;
        }

        handleResponse(response, request);
        Header etagHeader = response.getFirstHeader(HttpHeaders.ETAG);
        last = (etagHeader == null)
                ? null
                : new Memory(etagHeader.getValue(), response.getEntity());
        return response.getEntity();
    }

    /**
     * Performs an HTTP PUT request on the {@link Endpoint#getUri()}. Sets
     * {@link HttpHeaders#IF_MATCH} if there is a cached ETag to detect lost
     * updates.
     *
     * @param content The content to send to the server.
     * @return The response message.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The content has changed since it was last
     * retrieved with {@link #getContent()}. Your changes were rejected to
     * prevent a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    protected HttpResponse putContent(HttpEntity content)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        Request request = Request.Put(uri).body(content);
        if (last != null) {
            request.addHeader(HttpHeaders.IF_MATCH, last.etag);
        }

        return executeAndHandle(request);
    }

    /**
     * Performs an HTTP DELETE request on the {@link Endpoint#getUri()}. Sets
     * {@link HttpHeaders#IF_MATCH} if there is a cached ETag to detect lost
     * updates.
     *
     * @return The response message.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link #getContent()}. Your delete call was rejected to
     * prevent a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    protected HttpResponse deleteContent()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        Request request = Request.Delete(uri);
        if (last != null) {
            request.addHeader(HttpHeaders.IF_MATCH, last.etag);
        }

        return executeAndHandle(request);
    }
}
