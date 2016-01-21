package com.oneandone.typedrest.vaadin.forms;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.beans.*;

/**
 * An entity form that uses auto-generated fields.
 *
 * @param <TEntity> The type of entity the form operates on.
 */
public class AutoEntityForm<TEntity>
        extends AbstractEntityForm<TEntity> {

    /**
     * Creates a new entity form.
     *
     * @param entityType The type of entity the form operates on.
     */
    public AutoEntityForm(Class<TEntity> entityType) {
        super(entityType);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        for (PropertyDescriptor property : getPropertiesWithoutAnnotation(entityType, EditorHidden.class)) {
            if (property.getWriteMethod() != null) {
                layout.addComponent(fieldGroup.buildAndBind(property.getName()));
                getAnnotation(entityType, property, Description.class)
                        .ifPresent(x -> {
                            Label label = new Label(x.value());
                            label.addStyleName(ValoTheme.LABEL_LIGHT);
                            label.addStyleName(ValoTheme.LABEL_SMALL);
                            layout.addComponent(label);
                        });
            }
        }

        setCompositionRoot(layout);
    }
}
