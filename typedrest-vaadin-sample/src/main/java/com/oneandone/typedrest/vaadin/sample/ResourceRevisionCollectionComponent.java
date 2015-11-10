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
    protected Window buildCreateElementWindow() {
        return new CreateResourceRevisionElementComponent(endpoint).asWindow();
    }

    @Override
    protected Window buildUpdateElementWindow(ResourceRevisionElement elementEndpoint) {
        return new UpdateResourceRevisionElementComponent(elementEndpoint).asWindow();
    }
}
