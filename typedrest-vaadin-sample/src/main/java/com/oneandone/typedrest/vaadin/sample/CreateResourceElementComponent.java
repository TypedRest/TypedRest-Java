package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceCollection;
import com.oneandone.typedrest.sample.endpoints.ResourceElement;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.CreateElementComponent;

public class CreateResourceElementComponent extends CreateElementComponent<Resource, ResourceElement> {
    
    public CreateResourceElementComponent(ResourceCollection endpoint) {
        super(endpoint);
    }    
}
