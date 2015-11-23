package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.ui.Window;

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
     * @param lister A component for listing entity instances.
     */
    public StreamComponent(StreamEndpointImpl<TEntity> endpoint, EntityLister<TEntity> lister) {
        super(endpoint, lister);
    }

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public StreamComponent(StreamEndpointImpl<TEntity> endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildElementWindow(ElementEndpoint<TEntity> elementEndpoint) {
        return new ElementComponent<>(elementEndpoint).asWindow();
    }

    @Override
    protected Window buildCreateElementWindow() {
        return new CreateElementComponent<>(endpoint).asWindow();
    }
}
