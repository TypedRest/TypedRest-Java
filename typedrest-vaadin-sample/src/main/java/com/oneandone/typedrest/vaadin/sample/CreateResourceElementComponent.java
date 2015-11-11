package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.CollectionEndpoint;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class CreateResourceElementComponent
        extends CreateElementComponent<Resource, ResourceElement> {

    public CreateResourceElementComponent(CollectionEndpoint<Resource, ResourceElement> endpoint) {
        super(endpoint, new ResourceEditor());
    }
}
