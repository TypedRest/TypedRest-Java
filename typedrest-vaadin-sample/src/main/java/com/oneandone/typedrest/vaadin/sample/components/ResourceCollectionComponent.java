package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.Window;

public class ResourceCollectionComponent
        extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {

    public ResourceCollectionComponent(ResourceCollection endpoint) {
        super(endpoint);
        setCaption("Resources");
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
