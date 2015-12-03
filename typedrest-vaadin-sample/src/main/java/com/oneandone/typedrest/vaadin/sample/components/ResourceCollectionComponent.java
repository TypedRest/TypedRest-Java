package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class ResourceCollectionComponent
        extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {

    public ResourceCollectionComponent(ResourceCollection endpoint) {
        super(endpoint);
        setCaption("Resources");
    }

    @Override
    protected AbstractComponent buildElementComponent(ResourceElement elementEndpoint) {
        return new ResourceElementComponent(elementEndpoint);
    }

    @Override
    protected AbstractComponent buildCreateElementComponent() {
        return new CreateResourceElementComponent(endpoint);
    }
}
