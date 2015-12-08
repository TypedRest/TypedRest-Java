package com.oneandone.typedrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.oneandone.typedrest.URIUtils.*;
import java.io.*;
import java.net.*;
import javax.naming.OperationNotSupportedException;
import lombok.*;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.ContentType;
import org.apache.http.util.*;

/**
 * Base class for building REST endpoints, i.e. remote HTTP resources.
 */
public abstract class AbstractEndpoint
        implements Endpoint {

    @Getter
    protected final URI uri;

    @Getter
    protected final Executor rest;

    protected final ObjectMapper json = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .findAndRegisterModules();

    /**
     * Creates a new REST endpoint with an absolute URI.
     *
     * @param rest The REST executor used to communicate with the remote
     * element.
     * @param uri The HTTP URI of the remote element.
     */
    protected AbstractEndpoint(Executor rest, URI uri) {
        this.rest = rest;
        this.uri = uri;
    }

    /**
     * Creates a new REST endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     */
    protected AbstractEndpoint(Endpoint parent, URI relativeUri) {
        this(parent.getRest(), ensureTrailingSlash(parent.getUri()).resolve(relativeUri));
    }

    /**
     * Creates a new REST endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     */
    protected AbstractEndpoint(Endpoint parent, String relativeUri) {
        this(parent, URI.create(relativeUri));
    }

    /**
     * Executes a REST request and wraps HTTP status codes in appropriate
     * exception types.
     *
     * @param request The request to execute.
     * @return The HTTP response to the request.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws IllegalStateException
     * {@link HttpStatus#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     * @throws RuntimeException Other non-success status code.
     */
    protected HttpResponse execute(Request request)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, IllegalStateException {
        request.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        HttpResponse response = rest.execute(request).returnResponse();

        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() <= 299) {
            return response;
        }

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        Header encoding = entity.getContentType();
        String message = (encoding != null) && encoding.getValue().startsWith("application/json")
                ? json.readTree(body).get("message").asText()
                : statusLine.toString();

        switch (statusLine.getStatusCode()) {
            case HttpStatus.SC_BAD_REQUEST:
                throw new IllegalArgumentException(message, new HttpException(body));
            case HttpStatus.SC_UNAUTHORIZED:
            case HttpStatus.SC_FORBIDDEN:
                throw new IllegalAccessException(message);
            case HttpStatus.SC_NOT_FOUND:
            case HttpStatus.SC_GONE:
                throw new FileNotFoundException(message);
            case HttpStatus.SC_CONFLICT:
                throw new OperationNotSupportedException(message);
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                throw new IllegalStateException(message, new HttpException(body));
            default:
                throw new RuntimeException(message, new HttpException(body));
        }
    }
}
