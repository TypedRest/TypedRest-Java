package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.EntityLister;

/**
 * Component operating on an {@link StreamEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link StreamEndpointImpl}
 * represents.
 */
public class StreamComponent<TEntity>
        extends AbstractStreamComponent<TEntity, StreamEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    public StreamComponent(StreamEndpointImpl<TEntity> endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
    }

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public StreamComponent(StreamEndpointImpl<TEntity> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }

    @Override
    protected ViewComponent buildElementComponent(ElementEndpoint<TEntity> elementEndpoint) {
        return new ElementComponent<>(elementEndpoint, eventBus);
    }

    @Override
    protected ViewComponent buildCreateElementComponent() {
        return new CreateElementComponent<>(endpoint, eventBus);
    }
}
