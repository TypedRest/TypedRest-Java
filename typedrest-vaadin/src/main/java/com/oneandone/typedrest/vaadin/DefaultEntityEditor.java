package com.oneandone.typedrest.vaadin;

import com.vaadin.ui.*;

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
        for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
            layout.addComponent(fieldGroup.buildAndBind(propertyId));
        }
        setCompositionRoot(layout);
    }
}
