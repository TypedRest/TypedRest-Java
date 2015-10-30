package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceCollection;
import com.oneandone.typedrest.sample.endpoints.ResourceElement;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.AbstractCollectionComponent;
import com.vaadin.ui.Window;

public class ResourceCollectionComponent extends AbstractCollectionComponent<Resource, ResourceCollection, ResourceElement> {
    
    public ResourceCollectionComponent(ResourceCollection endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildCreateElementComponent() {
        return new CreateResourceElementComponent(endpoint);
    }

    @Override
    protected Window buildUpdateElementComponent(ResourceElement elementEndpoint) {
        return new UpdateResourceElementComponent(elementEndpoint);
    }
}
