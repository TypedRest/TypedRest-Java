package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class CreateResourceElementComponent
        extends CreateElementComponent<Resource, ResourceElement> {

    public CreateResourceElementComponent(ResourceCollection endpoint) {
        super(endpoint, new ResourceComponent(endpoint));
    }
}
