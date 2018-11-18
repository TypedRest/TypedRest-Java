package net.typedrest;

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
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public SupplierEndpointImpl(Endpoint referrer, URI relativeUri, Class<TResult> resultType) {
        super(referrer, relativeUri);
        this.resultType = resultType;
    }

    /**
     * Creates a new function endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Prefix <code>./</code> to append a trailing
     * slash to the <code>referrer</code> URI if missing.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public SupplierEndpointImpl(Endpoint referrer, String relativeUri, Class<TResult> resultType) {
        super(referrer, relativeUri);
        this.resultType = resultType;
    }

    @Override
    public TResult trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        HttpResponse response = executeAndHandle(Request.Post(uri));
        return serializer.readValue(EntityUtils.toString(response.getEntity()), resultType);
    }
}
