package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.ResourceRevisionElement;
import com.oneandone.typedrest.sample.models.ResourceRevision;
import com.oneandone.typedrest.vaadin.UpdateElementComponent;

public class UpdateResourceRevisionElementComponent extends UpdateElementComponent<ResourceRevision> {
    
    public UpdateResourceRevisionElementComponent(ResourceRevisionElement endpoint) {
        super(endpoint);
    }    
}
