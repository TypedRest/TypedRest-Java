package com.oneandone.typedrest.vaadin;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.*;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.data.util.*;
import com.vaadin.ui.*;

/**
 * Common base class for entity editor implementations.
 *
 * @param <TEntity> The type of entity the editor operates on.
 */
public abstract class AbstractEntityEditor<TEntity>
        extends CustomComponent implements EntityEditor<TEntity> {

    protected final Class<TEntity> entityType;
    protected final BeanFieldGroup<TEntity> fieldGroup;

    /**
     * Creates a new entity editor.
     *
     * @param entityType The type of entity the editor operates on.
     */
    protected AbstractEntityEditor(Class<TEntity> entityType) {
        this.entityType = entityType;
        this.fieldGroup = new BeanFieldGroup<>(entityType);
    }

    @Override
    public final TEntity getEntity() {
        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException e) {
            throw new RuntimeException(e);
        }
        return fieldGroup.getItemDataSource().getBean();
    }

    @Override
    public final void setEntity(TEntity entity) {
        fieldGroup.setItemDataSource(buildBeanItem(entity));
    }

    /**
     * Builds a {@link BeanItem} from an entity. Applies annotation-based
     * property filtering.
     *
     * @param entity the entity to wrap in the {@link BeanItem}.
     * @return the {@link BeanItem}.
     */
    protected BeanItem<TEntity> buildBeanItem(TEntity entity) {
        BeanItem<TEntity> beanItem = new BeanItem<>(entity, entityType);
        getPropertiesWithAnnotation(entityType, EditorHidden.class)
                .forEach(x -> beanItem.removeItemProperty(x.getName()));
        return beanItem;
    }
}
