package com.oneandone.typedrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.*;
import java.net.*;
import java.util.*;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.unmodifiableSet;
import java.util.concurrent.ConcurrentHashMap;
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
        this(parent.getRest(), parent.getUri().resolve(relativeUri));
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
     * {@link Exception} types.
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
        handleLinks(response);
        handleErrors(response);

        return response;
    }

    /**
     * Wraps HTTP status codes in appropriate {@link Exception} types.
     *
     * @param response The response to check for errors.
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
    protected void handleErrors(HttpResponse response)
            throws RuntimeException, IOException, IllegalAccessException, OperationNotSupportedException, FileNotFoundException, ParseException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() <= 299) {
            return;
        }

        String message = statusLine.toString();
        HttpEntity entity = response.getEntity();
        String body;
        if (entity == null) {
            body = null;
        } else {
            body = EntityUtils.toString(entity);
            Header contentType = entity.getContentType();
            if ((contentType != null) && contentType.getValue().startsWith("application/json")) {
                message = json.readTree(body).get("message").asText();
            }
        }

        Exception inner = (body == null) ? null : new HttpException(body);
        switch (statusLine.getStatusCode()) {
            case HttpStatus.SC_BAD_REQUEST:
                throw new IllegalArgumentException(message, inner);
            case HttpStatus.SC_UNAUTHORIZED:
            case HttpStatus.SC_FORBIDDEN:
                throw new IllegalAccessException(message);
            case HttpStatus.SC_NOT_FOUND:
            case HttpStatus.SC_GONE:
                throw new FileNotFoundException(message);
            case HttpStatus.SC_CONFLICT:
                throw new OperationNotSupportedException(message);
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                throw new IllegalStateException(message, inner);
            default:
                throw new RuntimeException(message, inner);
        }
    }

    /**
     * Handles HTTP Link headers.
     *
     * @param response The response to check for links.
     */
    private void handleLinks(HttpResponse response) {
        for (Header header : response.getHeaders("Link")) {
            for (HeaderElement element : header.getElements()) {
                NameValuePair relParameter = element.getParameterByName("rel");
                NameValuePair titleParameter = element.getParameterByName("title");
                handleLink(
                        element.getName().substring(1, element.getName().length() - 1),
                        (relParameter == null) ? null : relParameter.getValue(),
                        (titleParameter == null) ? null : titleParameter.getValue());
            }
        }
    }

    /**
     * Hook for handling links included in a response as an HTTP header.
     *
     * @param href The URI the link points to.
     * @param rel The relation type of the link; can be <code>null</code>.
     * @param title A human-readable description of the link; can be
     * <code>null</code>.
     */
    protected void handleLink(String href, String rel, String title) {
        if (rel == null) {
        } else if (rel.equals(notifyRel)) {
            notifyTargets.add(uri.resolve(href));
        } else if (rel.endsWith("-template")) {
        } else {
            links.put(rel, uri.resolve(href));
        }
    }

    private final ConcurrentHashMap<String, URI> links = new ConcurrentHashMap<>();

    @Override
    public URI link(String rel) {
        // Try to lazy-load missing link data
        if (links.isEmpty()) {
            try {
                handleLinks(rest.execute(Request.Get(uri)).returnResponse());
            } catch (IOException ex) {
                throw new RuntimeException("No link with rel=" + rel + " found in endpoint " + getUri() + ".", ex);
            }
        }

        URI href = links.get(rel);
        if (href == null) {
            throw new RuntimeException("No link with rel=" + rel + " found in endpoint " + getUri() + ".");
        }
        return href;
    }

    /**
     * The HTTP Link header relation type used by the server to set
     * {@link Endpoint#getNotifyTargets}.
     */
    @Getter
    @Setter
    private String notifyRel = "notify";

    private final Set<URI> notifyTargets = newSetFromMap(new ConcurrentHashMap<URI, Boolean>());

    @Override
    public Set<URI> getNotifyTargets() {
        return unmodifiableSet(notifyTargets);
    }
}
