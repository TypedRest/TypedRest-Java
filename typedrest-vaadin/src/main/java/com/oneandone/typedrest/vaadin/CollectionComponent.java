package com.oneandone.typedrest.vaadin;

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
     */
    public CollectionComponent(CollectionEndpointImpl<TEntity> endpoint) {
        super(endpoint);

        addElementClickListener(entity -> popUp(entity));
    }

    private void popUp(TEntity entity) {
        ElementComponent elementComponent = new ElementComponent(endpoint.get(entity));
        // TODO
    }
}
