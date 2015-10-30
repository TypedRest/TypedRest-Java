package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceRevisionCollection;
import com.oneandone.typedrest.sample.endpoints.ResourceRevisionElement;
import com.oneandone.typedrest.sample.models.ResourceRevision;
import com.oneandone.typedrest.vaadin.CreateElementComponent;

public class CreateResourceRevisionElementComponent extends CreateElementComponent<ResourceRevision, ResourceRevisionElement> {
    
    public CreateResourceRevisionElementComponent(ResourceRevisionCollection endpoint) {
        super(endpoint);
    }    
}
