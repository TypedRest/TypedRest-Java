package com.oneandone.typedrest.vaadin;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.*;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.data.util.*;
import com.vaadin.data.validator.*;
import com.vaadin.ui.*;
import java.lang.annotation.Annotation;

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
    public TEntity getEntity() {
        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException ex) {
            // replace exception for nicer message
            throw new Validator.InvalidValueException("Invalid input!");
        }
        return fieldGroup.getItemDataSource().getBean();
    }

    @Override
    public void setEntity(TEntity entity) {
        fieldGroup.setItemDataSource(buildBeanItem(entity));
        applyAnnotations(Required.class, new NullValidator("Must be set!", false));
        applyAnnotations(NotEmpty.class, new StringLengthValidator("Must not be empty!", 1, -1, false));
    }

    private void applyAnnotations(Class<? extends Annotation> annotationType, Validator validator) {
        getPropertiesWithAnnotation(entityType, annotationType)
                .forEach(property -> {
                    Field<?> field = fieldGroup.getField(property.getName());
                    if (field != null) {
                        field.addValidator(validator);
                    }
                });
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
