package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.AbstractCollectionEndpoint;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class CreateResourceRevisionElementComponent
        extends CreateElementComponent<ResourceRevision, ResourceRevisionElement> {

    public CreateResourceRevisionElementComponent(AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionElement> endpoint) {
        super(endpoint);
    }
}
