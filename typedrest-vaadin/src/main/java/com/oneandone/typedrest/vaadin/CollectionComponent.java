package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;

/**
 * Component operating on an {@link CollectionEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link CollectionEndpointImpl}
 * represents.
 */
public class CollectionComponent<TEntity>
        extends AbstractCollectionComponent<TEntity, CollectionEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    public CollectionComponent(CollectionEndpointImpl<TEntity> endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
    }

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public CollectionComponent(CollectionEndpointImpl<TEntity> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }

    @Override
    protected EndpointComponent buildElementComponent(ElementEndpoint<TEntity> elementEndpoint) {
        return new ElementComponent<>(elementEndpoint, eventBus);
    }

    @Override
    protected EndpointComponent buildCreateElementComponent() {
        return new CreateElementComponent<>(endpoint, eventBus);
    }
}
