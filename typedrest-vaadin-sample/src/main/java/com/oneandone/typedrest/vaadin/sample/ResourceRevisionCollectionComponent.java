package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceRevisionCollection;
import com.oneandone.typedrest.sample.endpoints.ResourceRevisionElement;
import com.oneandone.typedrest.sample.models.ResourceRevision;
import com.oneandone.typedrest.vaadin.AbstractCollectionComponent;
import com.vaadin.ui.Window;

public class ResourceRevisionCollectionComponent
        extends AbstractCollectionComponent<ResourceRevision, ResourceRevisionCollection, ResourceRevisionElement> {

    public ResourceRevisionCollectionComponent(ResourceRevisionCollection endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildCreateElementComponent() {
        return new CreateResourceRevisionElementComponent(endpoint);
    }

    @Override
    protected Window buildUpdateElementComponent(ResourceRevisionElement elementEndpoint) {
        return new UpdateResourceRevisionElementComponent(elementEndpoint);
    }
}
