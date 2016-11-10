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

        fieldGroup = new BeanFieldGroup<>(entityType);
        fieldGroup.setFieldFactory(new FieldGroupFieldFactory() {
            @Override
            public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
                return (fieldType.equals(Field.class))
                        ? (T) buildField(dataType)
                        : DefaultFieldGroupFieldFactory.get().createField(dataType, fieldType);
            }
        });
    }

    /**
     * Builds a field based on the data type to be edited.
     *
     * @param dataType The type to be edited using the field.
     * @return A field that is capable of editing the given type of data.
     */
    protected Field buildField(Class<?> dataType) {
        return DefaultFieldGroupFieldFactory.get().createField(dataType, Field.class);
    }

    @Override
    public TEntity getEntity()
            throws Validator.InvalidValueException {
        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException ex) {
            throw new Validator.InvalidValueException("Invalid input!");
        }
        return fieldGroup.getItemDataSource().getBean();
    }

    @Override
    public void setEntity(TEntity entity) {
        // Temporarily remove read-only flag to allow entity replacement
        boolean wasReadOnly = fieldGroup.isReadOnly();
        fieldGroup.setReadOnly(false);

        fieldGroup.setItemDataSource(new BeanItem<>(entity, entityType));
        applyAnnotations(Required.class, new NullValidator("Must be set!", false));
        applyAnnotations(NotEmpty.class, new StringLengthValidator("Must not be empty!", 1, -1, false));

        fieldGroup.setReadOnly(wasReadOnly);
    }

    private void applyAnnotations(Class<? extends Annotation> annotationType, Validator validator) {
        getPropertiesWithAnnotation(entityType, annotationType).forEach(property -> {
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
