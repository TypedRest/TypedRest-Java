package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;

/**
 * Component operating on an {@link PagedCollectionEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link PagedCollectionEndpointImpl}
 * represents.
 */
public class PagedCollectionComponent<TEntity>
        extends AbstractPagedCollectionComponent<TEntity, PagedCollectionEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param lister A component for listing entity instances.
     */
    public PagedCollectionComponent(PagedCollectionEndpointImpl<TEntity> endpoint, EntityLister<TEntity> lister) {
        super(endpoint, lister);
    }

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public PagedCollectionComponent(PagedCollectionEndpointImpl<TEntity> endpoint) {
        super(endpoint);
    }

    @Override
    protected EndpointComponent buildElementComponent(ElementEndpoint<TEntity> elementEndpoint) {
        return new ElementComponent<>(elementEndpoint);
    }

    @Override
    protected EndpointComponent buildCreateElementComponent() {
        return new CreateElementComponent<>(endpoint);
    }
}
