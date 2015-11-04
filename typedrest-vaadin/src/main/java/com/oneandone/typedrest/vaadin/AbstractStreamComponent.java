package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;

/**
 * Base class for building components operating on an {@link StreamEndpoint}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link StreamEndpoint} to operate on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractStreamComponent<TEntity, TEndpoint extends StreamEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionComponent<TEntity, TEndpoint, TElementEndpoint> {

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public AbstractStreamComponent(TEndpoint endpoint) {
        super(endpoint);
        setUpdateEnabled(false);
        setCreateEnabled(false);
        setDeleteEnabled(false);
    }
}
