package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.data.util.*;
import lombok.SneakyThrows;

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
    @SneakyThrows
    public static <TEntity> BeanItemContainer<TEntity> getAllBeans(CollectionEndpoint<TEntity, ?> endpoint) {
        return new BeanItemContainer<>(endpoint.getEntityType(), endpoint.readAll());
    }
}
