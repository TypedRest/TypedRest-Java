package com.oneandone.typedrest;

import static com.oneandone.typedrest.URIUtils.*;
import java.net.*;
import org.apache.http.auth.Credentials;
import org.apache.http.client.fluent.*;

/**
 * Entry point to a REST interface. Derive from this class and add your own set
 * of child-{@link RestEndpoint}s as properties.
 */
public class RestEntryPoint
        extends RestEndpointBase {

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     */
    public RestEntryPoint(URI uri) {
        super(
                Executor.newInstance(),
                ensureTrailingSlash(uri));
    }

    /**
     * Creates a new REST interface.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param credentials The credentials used to authenticate against the REST
     * interface.
     */
    public RestEntryPoint(URI uri, Credentials credentials) {
        super(
                Executor.newInstance().auth(credentials),
                ensureTrailingSlash(uri));
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
    public RestEntryPoint(URI uri, String username, String password) {
        super(
                Executor.newInstance().auth(username, password),
                ensureTrailingSlash(uri));
    }
}
