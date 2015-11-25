package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.data.util.*;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Utility methods for Java beans (classes with getters and setters).
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    /**
     * Returns all <code>TEntity</code>s provided by an
     * {@link CollectionEndpoint} wrapped in a {@link BeanItemContainer}.
     *
     * @param endpoint The endpoint to query.
     * @param <TEntity> The type of entity the endpoint represents.
     * @return The wrapped entities.
     */
    public static <TEntity> BeanItemContainer<TEntity> getAllBeans(CollectionEndpoint<TEntity, ?> endpoint) {
        try {
            return new BeanItemContainer<>(endpoint.getEntityType(), endpoint.readAll());
        } catch (IOException | IllegalAccessException | HttpException | OperationNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
