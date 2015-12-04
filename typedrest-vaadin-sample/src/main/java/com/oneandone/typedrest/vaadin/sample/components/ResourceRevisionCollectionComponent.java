package com.oneandone.typedrest.vaadin.sample.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class ResourceRevisionCollectionComponent
        extends AbstractCollectionComponent<ResourceRevision, ResourceRevisionCollection, ResourceRevisionElement> {

    public ResourceRevisionCollectionComponent(ResourceRevisionCollection endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        setCaption("Revisions");
    }

    @Override
    protected EndpointComponent buildElementComponent(ResourceRevisionElement elementEndpoint) {
        return new ResourceRevisionElementComponent(elementEndpoint, eventBus);
    }

    @Override
    protected EndpointComponent buildCreateElementComponent() {
        return new CreateResourceRevisionElementComponent(endpoint, eventBus);
    }
}
