package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.ElementEndpoint;

/**
 * Indicates that an existing element was updated.
 *
 * @param <TEntity> The type of entity that was updated.
 * @see ElementEndpoint#update(java.lang.Object)
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
