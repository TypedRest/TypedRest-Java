package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceElement;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.UpdateElementComponent;

public class UpdateResourceElementComponent extends UpdateElementComponent<Resource> {
    
    public UpdateResourceElementComponent(ResourceElement endpoint) {
        super(endpoint);
    }    
}
