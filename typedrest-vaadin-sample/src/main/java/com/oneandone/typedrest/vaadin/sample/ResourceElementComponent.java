package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.ElementEndpointImpl;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.ElementComponent;

public class ResourceElementComponent extends ElementComponent<Resource> {
    
    public ResourceElementComponent(ElementEndpointImpl<Resource> endpoint) {
        super(endpoint);
    }    
}
