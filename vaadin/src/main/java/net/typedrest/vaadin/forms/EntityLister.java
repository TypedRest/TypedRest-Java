package net.typedrest.vaadin.forms;

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
    default void setEntities(Collection<TEntity> entities) {
        clearEntities();
        addEntities(entities);
    }

    /**
     * Adds a set of entities to the list of entities the list shows.
     *
     * @param entities
     */
    void addEntities(Collection<TEntity> entities);

    /**
     * Removes all entities from the list.
     */
    void clearEntities();

    /**
     * Returns the number entities currently shown by the list.
     *
     * @return The number of entities.
     */
    int entityCount();

    /**
     * Registers a listener for entity click events.
     *
     * @param listener invoked when an entity is clicked.
     */
    void addEntityClickListener(EntityClickListener<TEntity> listener);

    /**
     * Scrolls to the end of the list.
     */
    void scrollToEnd();
}
