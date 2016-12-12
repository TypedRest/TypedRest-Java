package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.EntityLister;

/**
 * View component operating on an {@link CollectionEndpoint} with pagination.
 *
 * @param <TEntity> The type of entity the {@link CollectionEndpoint}
 * represents.
 */
public class PagedCollectionView<TEntity>
        extends AbstractPagedCollectionView<TEntity, CollectionEndpoint<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param lister A component for listing entity instances.
     * @param eventBus Used to send event between components.
     */
    public PagedCollectionView(CollectionEndpoint<TEntity> endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
    }

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    public PagedCollectionView(CollectionEndpoint<TEntity> endpoint, EventBus eventBus) {
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
