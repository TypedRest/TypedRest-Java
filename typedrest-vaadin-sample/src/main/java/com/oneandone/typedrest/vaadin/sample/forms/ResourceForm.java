package com.oneandone.typedrest.vaadin.sample.forms;

import com.oneandone.typedrest.CollectionEndpointImpl;
import com.oneandone.typedrest.Endpoint;
import com.oneandone.typedrest.sample.endpoints.ResourceCollection;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.sample.models.Target;
import com.oneandone.typedrest.vaadin.forms.AutoEntityForm;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;

public class ResourceForm
        extends AutoEntityForm<Resource> {

    private final Endpoint endpoint;

    public ResourceForm(Endpoint endpoint) {
        super(Resource.class);
        this.endpoint = endpoint;
    }

    private TargetCombobox targetCombobox;
    private TwinColSelect dependencySelector;

    @Override
    protected Component buildAndBind(PropertyDescriptor property) {
        switch (property.getName()) {
            case "target":
                targetCombobox = new TargetCombobox();
                fieldGroup.bind(targetCombobox, property.getName());
                return targetCombobox;

            case "dependencies":
                dependencySelector = new TwinColSelect("Dependencies");
                dependencySelector.setLeftColumnCaption("Available");
                dependencySelector.setRightColumnCaption("Selected");
                dependencySelector.setNullSelectionAllowed(true);
                // NOTE: Data binding is performed by setEntity()
                return dependencySelector;

            default:
                return super.buildAndBind(property);
        }
    }

    @Override
    public void setEntity(Resource entity) {
        try {
            targetCombobox.setItems(new CollectionEndpointImpl<>(endpoint, endpoint.link("targets"), Target.class).readAll());

            ResourceCollection resourcesEndpoint = (endpoint instanceof ResourceCollection)
                    ? (ResourceCollection) endpoint
                    : new ResourceCollection(endpoint);
            Collection<Resource> resources = resourcesEndpoint.readAll();
            resources.remove(entity); //Prevent self-references
            dependencySelector.setContainerDataSource(new BeanItemContainer<>(Resource.class, resources));
            fieldGroup.bind(dependencySelector, "dependencies");
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException ex) {
            UI.getCurrent().getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
        }

        super.setEntity(entity);
    }
}
