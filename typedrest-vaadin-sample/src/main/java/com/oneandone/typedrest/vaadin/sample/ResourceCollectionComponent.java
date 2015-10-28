package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.CollectionEndpointImpl;
import com.oneandone.typedrest.sample.models.Resource;
import com.oneandone.typedrest.vaadin.CollectionComponent;

public class ResourceCollectionComponent extends CollectionComponent<Resource> {
    
    public ResourceCollectionComponent(CollectionEndpointImpl<Resource> endpoint) {
        super(endpoint);
        
        setCaption("Resources");
    }    
}
