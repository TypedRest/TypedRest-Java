package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.ElementEndpoint;

/**
 * Indicates that an element was deleted.
 *
 * @param <TEntity> The type of entity that was deleted.
 * @see ElementEndpoint#delete()
 */
public class ElementDeletedEvent<TEntity> extends ElementEvent<TEntity> {

    /**
     * Creates a new element delete event.
     *
     * @param endpoint The endpoint representing the deleted entity.
     */
    public ElementDeletedEvent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint);
    }
}
