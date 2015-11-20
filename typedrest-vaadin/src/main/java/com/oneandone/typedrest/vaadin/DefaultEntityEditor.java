package com.oneandone.typedrest.vaadin;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.EditorHidden;
import com.vaadin.ui.*;
import java.beans.*;

/**
 * An entity editor that uses auto-generated forms.
 *
 * @param <TEntity> The type of entity the editor operates on.
 */
public class DefaultEntityEditor<TEntity>
        extends AbstractEntityEditor<TEntity> {

    /**
     * Creates a new entity editor.
     *
     * @param entityType The type of entity the editor operates on.
     */
    public DefaultEntityEditor(Class<TEntity> entityType) {
        super(entityType);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        for (PropertyDescriptor property : getPropertiesWithoutAnnotation(entityType, EditorHidden.class)) {
            if (property.getWriteMethod() != null) {
                layout.addComponent(fieldGroup.buildAndBind(property.getName()));
            }
        }

        setCompositionRoot(layout);
    }
}
