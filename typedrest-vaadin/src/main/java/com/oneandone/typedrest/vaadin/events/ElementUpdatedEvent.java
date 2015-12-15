package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.ElementEndpoint;

/**
 * Indicates that {@link ElementEndpoint#update(java.lang.Object)} was called.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class ElementUpdatedEvent<TEntity> extends ElementEvent<TEntity> {

    /**
     * Creates a new element update event.
     *
     * @param endpoint The endpoint representing the updated entity.
     */
    public ElementUpdatedEvent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint);
    }
}
