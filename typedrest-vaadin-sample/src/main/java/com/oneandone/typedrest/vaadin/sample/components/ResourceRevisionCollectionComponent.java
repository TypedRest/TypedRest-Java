package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class ResourceRevisionCollectionComponent
        extends AbstractCollectionComponent<ResourceRevision, ResourceRevisionCollection, ResourceRevisionElement> {

    public ResourceRevisionCollectionComponent(ResourceRevisionCollection endpoint) {
        super(endpoint);
        setCaption("Revisions");
    }

    @Override
    protected EndpointComponent buildElementComponent(ResourceRevisionElement elementEndpoint) {
        return new ResourceRevisionElementComponent(elementEndpoint);
    }

    @Override
    protected EndpointComponent buildCreateElementComponent() {
        return new CreateResourceRevisionElementComponent(endpoint);
    }
}
