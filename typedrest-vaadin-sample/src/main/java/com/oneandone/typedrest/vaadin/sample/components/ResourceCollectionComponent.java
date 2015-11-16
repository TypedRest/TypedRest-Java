package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.ResourceCollection;
import com.oneandone.typedrest.sample.endpoints.ResourceElement;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.AbstractCollectionComponent;
import com.vaadin.ui.Window;

public class ResourceCollectionComponent
        extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {

    public ResourceCollectionComponent(ResourceCollection endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildCreateElementWindow() {
        return new CreateResourceElementComponent(endpoint).asWindow();
    }

    @Override
    protected Window buildUpdateElementWindow(ResourceElement elementEndpoint) {
        return new UpdateResourceElementComponent(elementEndpoint).asWindow();
    }
}
