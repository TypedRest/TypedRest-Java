package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.components.EndpointComponent;
import com.oneandone.typedrest.vaadin.components.AbstractCollectionComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class ResourceCollectionComponent
        extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {

    public ResourceCollectionComponent(ResourceCollection endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        setCaption("Resources");
    }

    @Override
    protected EndpointComponent buildElementComponent(ResourceElement elementEndpoint) {
        return new ResourceElementComponent(elementEndpoint, eventBus);
    }

    @Override
    protected EndpointComponent buildCreateElementComponent() {
        return new CreateResourceElementComponent(endpoint, eventBus);
    }
}
