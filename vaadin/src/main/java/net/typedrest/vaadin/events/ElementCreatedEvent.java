package net.typedrest.vaadin.events;

import net.typedrest.CollectionEndpoint;
import net.typedrest.ElementEndpoint;

/**
 * Indicates that a new element was created.
 *
 * @param <TEntity> The type of entity that was created.
 * @see CollectionEndpoint#create(java.lang.Object)
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
