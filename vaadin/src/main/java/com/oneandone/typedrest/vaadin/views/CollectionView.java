package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.EntityLister;

/**
 * View component operating on an {@link CollectionEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link CollectionEndpointImpl}
 * represents.
 */
public class CollectionView<TEntity>
        extends AbstractCollectionView<TEntity, CollectionEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    public CollectionView(CollectionEndpointImpl<TEntity> endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
    }

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public CollectionView(CollectionEndpointImpl<TEntity> endpoint, EventBus eventBus) {
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