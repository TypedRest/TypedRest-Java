package com.oneandone.typedrest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import org.apache.http.HttpStatus;
import org.apache.http.auth.Credentials;
import org.apache.http.client.fluent.*;

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
        super(Executor.newInstance(), uri);
    }

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param credentials The credentials used to authenticate against the REST
     * interface.
     */
    public EntryEndpoint(URI uri, Credentials credentials) {
        super(Executor.newInstance().authPreemptive(uri.getHost()).auth(credentials), uri);
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
        super(Executor.newInstance().authPreemptive(uri.getHost()).auth(username, password), uri);
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
