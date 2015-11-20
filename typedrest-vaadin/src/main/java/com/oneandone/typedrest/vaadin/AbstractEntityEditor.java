package com.oneandone.typedrest.vaadin;

import static com.oneandone.typedrest.BeanUtils.getPropertiesWithAnnotation;
import com.oneandone.typedrest.EditorHidden;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;

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
