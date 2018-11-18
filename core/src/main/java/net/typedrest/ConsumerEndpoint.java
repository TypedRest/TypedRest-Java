package net.typedrest;

import java.io.*;
import org.apache.http.*;

/**
 * REST endpoint that represents an RPC-like action which takes
 * <code>TEntity</code> as input.
 *
 * @param <TEntity> The type of entity the endpoint takes as input.
 */
public interface ConsumerEndpoint<TEntity>
        extends TriggerEndpoint {

    /**
     * Returns the type of entity the endpoint takes as input.
     *
     * @return The class type.
     */
    Class<TEntity> getEntityType();

    /**
     * Triggers the action.
     *
     * @param entity The <code>TEntity</code> to post as input.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void trigger(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;
}
