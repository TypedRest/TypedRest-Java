package net.typedrest.vaadin.events;

import com.google.common.eventbus.EventBus;
import net.typedrest.ElementEndpoint;

/**
 * An event for the {@link EventBus} that references an {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public abstract class ElementEvent<TEntity>
        extends EndpointEvent<ElementEndpoint<TEntity>> {

    /**
     * Creates a new element event.
     *
     * @param endpoint The endpoint that raised the event.
     */
    protected ElementEvent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint);
    }
}
