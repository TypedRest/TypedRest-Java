package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.vaadin.annotations.Hidden;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;
import java.beans.IntrospectionException;
import static java.util.Arrays.stream;

/**
 * Common base class for entity editor implementations.
 *
 * @param <TEntity> The type of entity the editor operates on.
 */
public abstract class AbstractEntityEditor<TEntity>
        extends CustomComponent implements EntityEditor<TEntity> {

    private final Class<TEntity> entityType;
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
    public TEntity getEntity() {
        return fieldGroup.getItemDataSource().getBean();
    }

    @Override
    public void setEntity(TEntity entity) {
        BeanItem<TEntity> bean = new BeanItem<>(entity, entityType);
        try {
            stream(java.beans.Introspector.getBeanInfo(entityType).getPropertyDescriptors())
                    .filter(x -> x.getReadMethod().getAnnotation(Hidden.class) != null)
                    .forEach(x -> bean.removeItemProperty(x.getName()));
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        fieldGroup.setItemDataSource(bean);
    }
}
