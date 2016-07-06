package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import lombok.Getter;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.util.*;

/**
 * REST endpoint that represents a single RPC-like function which returns
 * <code>TResult</code>.
 *
 * @param <TResult> The type of entity the endpoint returns as output.
 */
public class SupplierEndpointImpl<TResult>
        extends AbstractTriggerEndpoint
        implements SupplierEndpoint<TResult> {

    @Getter
    private final Class<TResult> resultType;

    /**
     * Creates a new function endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public SupplierEndpointImpl(Endpoint parent, URI relativeUri, Class<TResult> resultType) {
        super(parent, relativeUri);
        this.resultType = resultType;
    }

    /**
     * Creates a new function endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Prefix <code>./</code> to append a trailing slash
     * to the parent URI if missing.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public SupplierEndpointImpl(Endpoint parent, String relativeUri, Class<TResult> resultType) {
        super(parent, relativeUri);
        this.resultType = resultType;
    }

    @Override
    public TResult trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        HttpResponse response = executeAndHandle(Request.Post(uri));
        return serializer.readValue(EntityUtils.toString(response.getEntity()), resultType);
    }
}
