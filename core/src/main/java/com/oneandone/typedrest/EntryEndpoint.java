package com.oneandone.typedrest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import static com.oneandone.typedrest.URIUtils.ensureTrailingSlash;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Base64;
import lombok.SneakyThrows;
import static org.apache.http.HttpHeaders.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import org.apache.http.message.BasicHeader;

/**
 * Entry point to a REST interface. Derive from this class and add your own set
 * of child-{@link Endpoint}s as properties.
 */
public class EntryEndpoint
        extends AbstractEndpoint {

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     */
    public EntryEndpoint(URI uri) {
        this(uri, defaultSerializer());
        setAcceptJsonHeader();
    }

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param serializer Controls the serialization of entities sent to and
     * received from the server.
     */
    public EntryEndpoint(URI uri, ObjectMapper serializer) {
        super(ensureTrailingSlash(uri), Executor.newInstance(), serializer);
    }

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param username The username used to authenticate against the REST
     * interface.
     * @param password The password used to authenticate against the REST
     * interface.
     */
    public EntryEndpoint(URI uri, String username, String password) {
        this(uri, username, password, defaultSerializer());
        setAcceptJsonHeader();
    }

    private static ObjectMapper defaultSerializer() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .findAndRegisterModules();
    }

    private void setAcceptJsonHeader() {
        defaultHeaders.add(new BasicHeader(ACCEPT, APPLICATION_JSON.getMimeType()));
    }

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param username The username used to authenticate against the REST
     * interface.
     * @param password The password used to authenticate against the REST
     * interface.
     * @param serializer Controls the serialization of entities sent to and
     * received from the server.
     */
    public EntryEndpoint(URI uri, String username, String password, ObjectMapper serializer) {
        super(ensureTrailingSlash(uri), Executor.newInstance(), serializer);
        setBasicAuthHeader(username, password);
    }

    @SneakyThrows
    private void setBasicAuthHeader(String username, String password) {
        byte[] bytesEncoded = Base64.getEncoder().encode((username + ":" + password).getBytes("ISO-8859-1"));
        defaultHeaders.add(new BasicHeader(AUTHORIZATION, "Basic " + new String(bytesEncoded)));
    }

    /**
     * Fetches meta data such as links from the server.
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
    public void readMeta()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        executeAndHandle(Request.Get(uri));
    }
}
