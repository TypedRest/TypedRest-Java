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
    protected Window buildElementWindow(ResourceElement elementEndpoint) {
        return new ResourceElementComponent(elementEndpoint).asWindow();
    }

    @Override
    protected Window buildCreateElementWindow() {
        return new CreateResourceElementComponent(endpoint).asWindow();
    }
}
