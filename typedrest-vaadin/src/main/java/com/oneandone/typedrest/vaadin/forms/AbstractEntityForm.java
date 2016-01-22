package com.oneandone.typedrest.vaadin.forms;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.*;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.data.util.*;
import com.vaadin.data.validator.*;
import com.vaadin.ui.*;
import java.lang.annotation.Annotation;

/**
 * Base class for building entity form implementations.
 *
 * @param <TEntity> The type of entity the form operates on.
 */
public abstract class AbstractEntityForm<TEntity>
        extends CustomComponent implements EntityForm<TEntity> {

    protected final Class<TEntity> entityType;
    protected final BeanFieldGroup<TEntity> fieldGroup;

    /**
     * Creates a new entity form.
     *
     * @param entityType The type of entity the form operates on.
     */
    protected AbstractEntityForm(Class<TEntity> entityType) {
        this.entityType = entityType;
        this.fieldGroup = new BeanFieldGroup<>(entityType);
        setSizeFull();
        setWidth(100, Unit.PERCENTAGE);
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
        fieldGroup.setItemDataSource(new BeanItem<>(entity, entityType));
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

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        fieldGroup.setReadOnly(readOnly);
    }
}
