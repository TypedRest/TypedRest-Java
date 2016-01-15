package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.components.AbstractCollectionComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.components.ViewComponent;

public class ResourceCollectionComponent
        extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {

    public ResourceCollectionComponent(ResourceCollection endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        setCaption("Resources");
    }

    @Override
    protected ViewComponent buildElementComponent(ResourceElement elementEndpoint) {
        return new ResourceElementComponent(elementEndpoint, eventBus);
    }

    @Override
    protected ViewComponent buildCreateElementComponent() {
        return new CreateResourceElementComponent(endpoint, eventBus);
    }
}
