package com.oneandone.typedrest;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import static com.oneandone.typedrest.HeaderUtils.getLinkHeaders;
import static com.oneandone.typedrest.URIUtils.ensureTrailingSlash;
import java.io.*;
import java.net.*;
import java.util.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import lombok.*;
import org.apache.http.*;
import static org.apache.http.HttpHeaders.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.*;
import static java.util.Arrays.stream;

/**
 * Base class for building REST endpoints, i.e. remote HTTP resources.
 */
public abstract class AbstractEndpoint
        implements Endpoint {

    @Getter
    protected final URI uri;

    @Getter
    protected final Executor executor;

    @Getter
    protected final ObjectMapper serializer;

    /**
     * A set of default HTTP headers to be added to each request.
     */
    protected final Collection<Header> defaultHeaders = new LinkedList<>();

    /**
     * Creates a new REST endpoint with an absolute URI.
     *
     * @param uri The HTTP URI of the remote element.
     * @param executor The REST executor used to communicate with the remote
     * element.
     * @param serializer Controls the serialization of entities sent to and
     * received from the server.
     */
    protected AbstractEndpoint(URI uri, Executor executor, ObjectMapper serializer) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null.");
        }
        if (serializer == null) {
            throw new IllegalArgumentException("serializer must not be null.");
        }

        this.uri = uri;
        this.executor = executor;
        this.serializer = serializer;
    }

    /**
     * Creates a new REST endpoint with a relative URI.
     *
     * @param referrer The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s.
     */
    protected AbstractEndpoint(Endpoint referrer, URI relativeUri) {
        this(referrer.getUri().resolve(relativeUri), referrer.getExecutor(), referrer.getSerializer());

        if (referrer instanceof AbstractEndpoint) {
            defaultHeaders.addAll(((AbstractEndpoint) referrer).defaultHeaders);
        }
    }

    /**
     * Creates a new REST endpoint with a relative URI.
     *
     * @param referrer The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Prefix <code>./</code> to append a trailing
     * slash to the <code>referrer</code> URI if missing. Prefix <code>./</code> to
     * append a trailing slash to the <code>referrer</code> URI if missing.
     */
    protected AbstractEndpoint(Endpoint referrer, String relativeUri) {
        this((relativeUri.startsWith("./") ? ensureTrailingSlash(referrer.getUri()) : referrer.getUri())
                .resolve(relativeUri), referrer.getExecutor(), referrer.getSerializer());

        if (referrer instanceof AbstractEndpoint) {
            defaultHeaders.addAll(((AbstractEndpoint) referrer).defaultHeaders);
        }
    }

    /**
     * Registers one or more default links for a specific relation type. These
     * links are used when no links with this relation type are provided by the
     * server.
     *
     * This method is not thread-safe! Call this before performing any requests.
     *
     * @param rel The relation type of the links to add.
     * @param hrefs The hrefs of links relative to this endpoint's URI. Use
     * <code>null</code> or an empty list to remove all previous entries for the
     * relation type.
     *
     * @see Endpoint#getLinks(java.lang.String)
     * @see Endpoint#getLinksWithTitles(java.lang.String)
     * @see Endpoint#link(java.lang.String)
     */
    public final void setDefaultLink(String rel, String... hrefs) {
        if (hrefs == null || hrefs.length == 0) {
            defaultLinks.remove(rel);
        } else {
            defaultLinks.put(rel,
                    stream(hrefs).map(uri::resolve).collect(toSet()));
        }
    }

    /**
     * Registers a default link template for a specific relation type. This
     * template is used when no template with this relation type is provided by
     * the server.
     *
     * This method is not thread-safe! Call this before performing any requests.
     *
     * @param rel The relation type of the link template to add.
     * @param href The href of the link template relative to this endpoint's
     * URI. Use <code>null</code> to remove any previous entry for the relation
     * type.
     *
     * @see Endpoint#linkTemplate(java.lang.String)
     */
    public final void setDefaultLinkTemplate(String rel, String href) {
        if (href == null) {
            defaultLinkTemplates.remove(rel);
        } else {
            defaultLinkTemplates.put(rel, href);
        }
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
     * @throws IllegalStateException
     * {@link HttpStatus#SC_CONFLICT}, {@link HttpStatus#SC_PRECONDITION_FAILED}
     * or {@link HttpStatus#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     * @throws RuntimeException Other non-success status code.
     */
    protected HttpResponse executeAndHandle(Request request)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        HttpResponse response = execute(request);
        handleResponse(response, request);
        return response;
    }

    /**
     * Executes a REST request adding any configured {@link #defaultHeaders}.
     *
     * @param request The request to execute.
     * @return The HTTP response to the request.
     *
     * @throws IOException Network communication failed.
     * @throws RuntimeException Other non-success status code.
     */
    protected HttpResponse execute(Request request)
            throws IOException {
        defaultHeaders.forEach(request::addHeader);
        return executor.execute(request).returnResponse();
    }

    /**
     * Handles the response of a REST request and wraps HTTP status codes in
     * appropriate {@link Exception} types.
     *
     * @param response The response to handle.
     * @param request The original request the reponse is for.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException
     * {@link HttpStatus#SC_CONFLICT}, {@link HttpStatus#SC_PRECONDITION_FAILED}
     * or {@link HttpStatus#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     * @throws RuntimeException Other non-success status code.
     */
    protected void handleResponse(HttpResponse response, Request request)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        handleLinks(response);
        handleAllow(response);
        handleErrors(response, request);
    }

    /**
     * Wraps HTTP status codes in appropriate {@link Exception} types.
     *
     * @param response The response to check for errors.
     * @param request The original request the reponse is for.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException
     * {@link HttpStatus#SC_CONFLICT}, {@link HttpStatus#SC_PRECONDITION_FAILED}
     * or {@link HttpStatus#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     * @throws RuntimeException Other non-success status code.
     */
    protected void handleErrors(HttpResponse response, Request request)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() <= 299) {
            return;
        }

        String message = request + " responded with " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();

        HttpEntity entity = response.getEntity();
        String body;
        if (entity == null) {
            body = null;
        } else {
            body = EntityUtils.toString(entity);
            Header contentType = entity.getContentType();
            if ((contentType != null) && contentType.getValue().startsWith("application/json")) {
                try {
                    JsonNode messageNode = serializer.readTree(body).get("message");
                    if (messageNode != null) {
                        message = messageNode.asText();
                    }
                } catch (JsonProcessingException ex) {
                }
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
            case HttpStatus.SC_PRECONDITION_FAILED:
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                throw new IllegalStateException(message, inner);
            default:
                throw new RuntimeException(message, inner);
        }
    }

    /**
     * Handles links embedded in an HTTP response.
     *
     * @param response The response to check for links.
     */
    @SuppressWarnings("LocalVariableHidesMemberVariable")
    private void handleLinks(HttpResponse response) {
        Map<String, Map<URI, String>> links = new HashMap<>();
        Map<String, String> linkTemplates = new HashMap<>();

        handleHeaderLinks(response, links, linkTemplates);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Header contentType = entity.getContentType();
            if ((contentType != null) && contentType.getValue().startsWith("application/json")) {
                try {
                    handleBodyLinks(serializer.readTree(entity.getContent()), links, linkTemplates);
                } catch (IOException ex) {
                    throw new RuntimeException();
                    // Body error handling is done elsewhere
                }
            }
        }

        this.links = unmodifiableMap(links);
        this.linkTemplates = unmodifiableMap(linkTemplates);
    }

    /**
     * Handles links embedded in HTTP response headers.
     *
     * @param response The response to check for links.
     * @param links A dictionary to add found links to.
     * @param linkTemplates A dictionary to add found link templates to.
     */
    protected void handleHeaderLinks(HttpResponse response, Map<String, Map<URI, String>> links, Map<String, String> linkTemplates) {
        for (LinkHeader header : getLinkHeaders(response)) {
            if (header.getRel() != null) {
                if (header.isTemplated()) {
                    linkTemplates.put(header.getRel(), header.getHref());
                } else {
                    getOrAdd(links, header.getRel())
                            .put(uri.resolve(header.getHref()), header.getTitle());
                }
            }
        }
    }

    /**
     * Handles links embedded in JSON response bodies.
     *
     * @param jsonBody The body to check for links.
     * @param links A dictionary to add found links to.
     * @param linkTemplates A dictionary to add found link templates to.
     */
    protected void handleBodyLinks(JsonNode jsonBody, Map<String, Map<URI, String>> links, Map<String, String> linkTemplates) {
        if (jsonBody.getNodeType() != JsonNodeType.OBJECT) {
            return;
        }

        JsonNode linksNode = jsonBody.get("_links");
        if (linksNode == null) {
            linksNode = jsonBody.get("links");
        }
        if (linksNode == null) {
            return;
        }

        linksNode.fields().forEachRemaining(x -> {
            String rel = x.getKey();
            Map<URI, String> linksForRel = getOrAdd(links, rel);

            switch (x.getValue().getNodeType()) {
                case ARRAY:
                    for (JsonNode subobj : x.getValue()) {
                        if (subobj.getNodeType() == JsonNodeType.OBJECT) {
                            parseLinkObject(rel, (ObjectNode) subobj, linksForRel, linkTemplates);
                        }
                    }
                    break;
                case OBJECT:
                    parseLinkObject(rel, (ObjectNode) x.getValue(), linksForRel, linkTemplates);
                    break;
            }
        });
    }

    /**
     * Parses a JSON object for link information.
     *
     * @param rel The relation type of the link.
     * @param obj The JSON object to parse for link information.
     * @param linksForRel A dictionary to add found links to. Maps hrefs to
     * titles.
     * @param linkTemplates A dictionary to add found link templates to. Maps
     * rels to templated hrefs.
     */
    private void parseLinkObject(String rel, ObjectNode obj, Map<URI, String> linksForRel, Map<String, String> linkTemplates) {
        JsonNode href = obj.findValue("href");
        if (href == null) {
            return;
        }

        JsonNode templated = obj.findValue("templated");
        if (templated != null && templated.isBoolean() && templated.asBoolean()) {
            linkTemplates.put(rel, href.asText());
        } else {
            JsonNode title = obj.findValue("title");
            linksForRel.put(
                    uri.resolve(href.asText()),
                    (title != null && title.getNodeType() == JsonNodeType.STRING) ? title.asText() : null);
        }
    }

    /**
     * Returns the element with the specified key from the map. Creates, adds
     * and returns a new element if no match was found.
     *
     * @param map The map to look in.
     * @param key The key to look for.
     * @return The existing or new element.
     */
    private static Map<URI, String> getOrAdd(Map<String, Map<URI, String>> map, String key) {
        Map<URI, String> value = map.get(key);
        if (value == null) {
            map.put(key, value = new HashMap<>());
        }
        return value;
    }

    // NOTE: Always replace entire dictionary rather than modifying it to ensure thread-safety.
    private Map<String, Map<URI, String>> links = unmodifiableMap(new HashMap<>());

    // NOTE: Only modify during initial setup
    private final Map<String, Set<URI>> defaultLinks = new HashMap<>();

    @Override
    public Set<URI> getLinks(String rel) {
        Map<URI, String> linksForRel = links.get(rel);
        if (linksForRel != null) {
            return linksForRel.keySet();
        }

        Set<URI> defaulLinksForRel = defaultLinks.get(rel);
        if (defaulLinksForRel != null) {
            return defaulLinksForRel;
        }

        return new HashSet<>();
    }

    @Override
    public Map<URI, String> getLinksWithTitles(String rel) {
        Map<URI, String> linksForRel = links.get(rel);
        if (linksForRel != null) {
            return linksForRel;
        }

        Set<URI> defaulLinksForRel = defaultLinks.get(rel);
        if (defaulLinksForRel != null) {
            Map<URI, String> result = new HashMap<>();
            for (URI link : defaulLinksForRel) {
                result.put(link, null);
            }
            return result;
        }

        return new HashMap<>();
    }

    @Override
    public URI link(String rel) {
        Set<URI> linkSet = getLinks(rel);
        if (linkSet.isEmpty()) {
            // Lazy lookup
            try {
                executeAndHandle(Request.Head(uri));
            } catch (IOException | IllegalAccessException | RuntimeException ex) {
                throw new RuntimeException("No link with rel=" + rel + " provided by endpoint " + getUri() + ".", ex);
            }

            linkSet = getLinks(rel);
            if (linkSet.isEmpty()) {
                throw new RuntimeException("No link with rel=" + rel + " provided by endpoint " + getUri() + ".");
            }
        }

        return linkSet.iterator().next();
    }

    // NOTE: Always replace entire dictionary rather than modifying it to ensure thread-safety.
    private Map<String, String> linkTemplates = unmodifiableMap(new HashMap<>());

    // NOTE: Only modify during initial setup
    private final Map<String, String> defaultLinkTemplates = new HashMap<>();

    @Override
    public UriTemplate linkTemplate(String rel) {
        String template = linkTemplates.get(rel);
        if (template == null) {
            template = defaultLinkTemplates.get(rel);
        }
        if (template == null) {
            // Lazy lookup
            try {
                executeAndHandle(Request.Head(uri));
            } catch (IOException | IllegalAccessException | RuntimeException ex) {
                // HTTP HEAD server-side implementation is optional
            }

            if (linkTemplates != null) {
                template = linkTemplates.get(rel);
                if (template == null) {
                    throw new RuntimeException("No link template with rel=" + rel + " provided by endpoint " + getUri() + ".");
                }
            }
        }

        // Create new template instance for each request because UriTemplate is not immutable
        return UriTemplate.fromTemplate(template);
    }

    /**
     * Handles allowed HTTP verbs reported by the server.
     *
     * @param response The response to check for the "Allow" header.
     */
    private void handleAllow(HttpResponse response) {
        allowedVerbs = unmodifiableSet(stream(response.getHeaders("Allow"))
                .filter(x -> x.getName().equals("Allow"))
                .flatMap(x -> stream(x.getElements())).map(x -> x.getName())
                .collect(toSet()));
    }

    // NOTE: Always replace entire set rather than modifying it to ensure thread-safety.
    private Set<String> allowedVerbs = unmodifiableSet(new HashSet<>());

    /**
     * Shows whether the server has indicated that a specific HTTP verb is
     * currently allowed.
     *
     * Uses cached data from last response.
     *
     * @param verb The HTTP verb (e.g. GET, POST, ...) to check.
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
     * {@link Optional#empty()} is returned.
     */
    protected Optional<Boolean> isVerbAllowed(String verb) {
        if (allowedVerbs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(allowedVerbs.contains(verb));
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + uri;
    }
}
