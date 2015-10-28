package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;

/**
 * Component operating on an {@link StreamEndpointImpl}.
 *
 * @param <TEntity> The type of entity the {@link StreamEndpointImpl}
 * represents.
 */
public class StreamComponent<TEntity>
        extends AbstractStreamComponent<TEntity, StreamEndpointImpl<TEntity>, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public StreamComponent(StreamEndpointImpl<TEntity> endpoint) {
        super(endpoint);
        
        addElementClickListener(entity -> popUp(entity));
    }

    private void popUp(TEntity entity) {
        ElementComponent elementComponent = new ElementComponent(endpoint.get(entity));
        // TODO
    }
}
