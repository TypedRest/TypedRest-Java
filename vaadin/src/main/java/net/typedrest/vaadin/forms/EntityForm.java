package net.typedrest.vaadin.forms;

import com.vaadin.data.Validator;
import com.vaadin.ui.Component;

/**
 * Vaadin component for presenting/editing entity instances.
 *
 * @param <TEntity> The type of entity the from operates on.
 */
public interface EntityForm<TEntity> extends Component {

    /**
     * Returns the entity the form operates on.
     *
     * @throws Validator.InvalidValueException The user input is invalid.
     * @return the entity the form operates on.
     */
    TEntity getEntity()
            throws Validator.InvalidValueException;

    /**
     * Sets the entity the form operates on. This must be called before the form
     * can be used, even when creating new entities.
     *
     * @param entity the entity the form operates on.
     */
    void setEntity(TEntity entity);
}
