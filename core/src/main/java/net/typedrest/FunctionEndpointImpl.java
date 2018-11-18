package net.typedrest;

import java.io.*;
import java.net.*;
import lombok.Getter;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.ContentType;
import org.apache.http.util.*;

/**
 * REST endpoint that represents an RPC-like function which takes
 * <code>TEntity</code> as input and returns <code>TResult</code> as output.
 *
 * @param <TEntity> The type of entity the endpoint takes as input.
 * @param <TResult> The type of entity the endpoint returns as output.
 */
public class FunctionEndpointImpl<TEntity, TResult>
        extends AbstractTriggerEndpoint
        implements FunctionEndpoint<TEntity, TResult> {

    @Getter
    private final Class<TEntity> entityType;

    @Getter
    private final Class<TResult> resultType;

    /**
     * Creates a new function endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public FunctionEndpointImpl(Endpoint referrer, URI relativeUri, Class<TEntity> entityType, Class<TResult> resultType) {
        super(referrer, relativeUri);
        this.entityType = entityType;
        this.resultType = resultType;
    }

    /**
     * Creates a new function endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Prefix <code>./</code> to append a trailing
     * slash to the <code>referrer</code> URI if missing.
     * @param entityType The type of entity the endpoint takes as input.
     * @param resultType The type of entity the endpoint returns as output.
     */
    public FunctionEndpointImpl(Endpoint referrer, String relativeUri, Class<TEntity> entityType, Class<TResult> resultType) {
        super(referrer, relativeUri);
        this.entityType = entityType;
        this.resultType = resultType;
    }

    @Override
    public TResult trigger(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null.");
        }

        String jsonSend = serializer.writeValueAsString(entity);
        HttpResponse response = executeAndHandle(Request.Post(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
        return serializer.readValue(EntityUtils.toString(response.getEntity()), resultType);
    }
}
