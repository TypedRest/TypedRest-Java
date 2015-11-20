package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.AbstractEntityEditor;
import com.vaadin.ui.*;

public class ResourceEditor
        extends AbstractEntityEditor<Resource> {

    public ResourceEditor() {
        super(Resource.class);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(fieldGroup.buildAndBind("name"));
        setCompositionRoot(layout);
    }
}
