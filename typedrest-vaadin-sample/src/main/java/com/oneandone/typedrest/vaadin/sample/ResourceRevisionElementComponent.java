package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.ElementEndpointImpl;
import com.oneandone.typedrest.sample.models.ResourceRevision;
import com.oneandone.typedrest.vaadin.ElementComponent;

public class ResourceRevisionElementComponent extends ElementComponent<ResourceRevision> {
    
    public ResourceRevisionElementComponent(ElementEndpointImpl<ResourceRevision> endpoint) {
        super(endpoint);
    }    
}
