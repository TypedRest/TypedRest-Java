package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.CollectionEndpointImpl;
import com.oneandone.typedrest.sample.models.ResourceRevision;
import com.oneandone.typedrest.vaadin.CollectionComponent;

public class ResourceRevisionCollectionComponent extends CollectionComponent<ResourceRevision> {
    
    public ResourceRevisionCollectionComponent(CollectionEndpointImpl<ResourceRevision> endpoint) {
        super(endpoint);
        
        setCaption("Resource revisions");
    }    
}
