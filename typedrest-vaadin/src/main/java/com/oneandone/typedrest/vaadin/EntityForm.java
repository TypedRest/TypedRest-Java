package com.oneandone.typedrest.vaadin;

import com.vaadin.ui.Component;

/**
 * Vaadin component for presenting/editing entity instances.
 *
 * @param <TEntity> The type of entity the editor operates on.
 */
public interface EntityForm<TEntity> extends Component {

    /**
     * Returns the entity the component operates on.
     *
     * @return the entity the component operates on.
     */
    TEntity getEntity();

    /**
     * Sets the entity the component operates on. This must be called before the
     * component can be used, even when creating new entities.
     *
     * @param entity the entity the component operates on.
     */
    void setEntity(TEntity entity);
}
