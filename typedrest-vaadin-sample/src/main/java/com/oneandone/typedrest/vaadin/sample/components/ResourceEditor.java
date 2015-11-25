package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.ResourceCollection;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.AbstractEntityEditor;
import static com.oneandone.typedrest.vaadin.BeanUtils.getAllBeans;
import com.vaadin.ui.*;

public class ResourceEditor
        extends AbstractEntityEditor<Resource> {

    public ResourceEditor(ResourceCollection resources) {
        super(Resource.class);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(fieldGroup.buildAndBind("name"));
        TwinColSelect dependencies = new TwinColSelect("Dependencies");
        dependencies.setContainerDataSource(getAllBeans(resources));
        fieldGroup.bind(dependencies, "dependencies");
        layout.addComponent(dependencies);

        setCompositionRoot(layout);
    }
}
