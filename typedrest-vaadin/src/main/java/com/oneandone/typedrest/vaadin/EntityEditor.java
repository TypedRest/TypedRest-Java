package com.oneandone.typedrest.vaadin;

import com.vaadin.ui.Component;

/**
 * Vaadin component for editing entity instances.
 *
 * @param <TEntity> The type of entity the editor operates on.
 */
public interface EntityEditor<TEntity> extends Component {

    /**
     * Returns the entity the editor operates on.
     *
     * @return the entity the editor operates on.
     */
    TEntity getEntity();

    /**
     * Sets the entity the editor operates on. This must be called before the
     * editor can be used, even when creating new entities.
     *
     * @param entity the entity the editor operates on.
     */
    void setEntity(TEntity entity);
}
