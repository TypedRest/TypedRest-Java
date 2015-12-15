package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.CollectionEndpoint;
import com.oneandone.typedrest.ElementEndpoint;

/**
 * Indicates that {@link CollectionEndpoint#create(java.lang.Object)} was
 * called. Reports the resulting {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class ElementCreatedEvent<TEntity> extends ElementEvent<TEntity> {

    /**
     * Creates a new element created event.
     *
     * @param endpoint The endpoint representing the newly created entity.
     */
    public ElementCreatedEvent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint);
    }
}
