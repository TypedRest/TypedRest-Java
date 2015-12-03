package com.oneandone.typedrest.vaadin;

import com.vaadin.ui.Component;
import java.util.Collection;

/**
 * Vaadin component for listing entities.
 *
 * @param <TEntity> The type of entities the list shows.
 */
public interface EntityLister<TEntity> extends Component {

    /**
     * Sets the entities entities the list shows.
     *
     * @param entities the entities the list shows.
     */
    void setEntities(Collection<TEntity> entities);

    /**
     * Adds an entity to the list of entities the list shows.
     *
     * @param entity the entity to add.
     */
    void addEntity(TEntity entity);
    
    /**
     * Controls whether checkboxes for selecting entities are shown.
     *
     * @param val Turns the feature on or off.
     */
    void setSelectionEnabled(boolean val);

    /**
     * Returns all entities currently selected in the list.
     *
     * @return the currently selected entities.
     */
    Collection<TEntity> getSelectedEntities();

    /**
     * Registers a listener for entity click events.
     *
     * @param listener invoked when an entity is clicked.
     */
    void addEntityClickListener(EntityClickListener<TEntity> listener);
}
