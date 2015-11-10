package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.ui.Window;

/**
 * Component operating on an {@link PagedCollectionEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link PagedCollectionEndpointImpl}
 * represents.
 */
public class PagedCollectionComponent<TEntity>
        extends AbstractPagedCollectionComponent<TEntity, PagedCollectionEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public PagedCollectionComponent(PagedCollectionEndpointImpl<TEntity> endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildCreateElementWindow() {
        return new CreateElementComponent<>(endpoint);
    }

    @Override
    protected Window buildUpdateElementWindow(ElementEndpoint<TEntity> elementEndpoint) {
        return new UpdateElementComponent<>(elementEndpoint);
    }
}
