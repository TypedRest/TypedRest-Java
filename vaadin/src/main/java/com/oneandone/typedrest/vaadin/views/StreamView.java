package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.EntityLister;

/**
 * View component operating on a {@link StreamEndpoint}.
 *
 * @param <TEntity> The type of entity the {@link StreamEndpoint} represents.
 */
public class StreamView<TEntity>
        extends AbstractStreamView<TEntity, StreamEndpoint<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     * @param lister A component for listing entity instances.
     */
    public StreamView(StreamEndpoint<TEntity> endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
    }

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    public StreamView(StreamEndpoint<TEntity> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }

    @Override
    protected ViewComponent buildElementView(ElementEndpoint<TEntity> elementEndpoint) {
        return new ElementView<>(elementEndpoint, eventBus);
    }

    @Override
    protected ViewComponent buildCreateElementView() {
        return new CreateElementView<>(endpoint, eventBus);
    }
}
