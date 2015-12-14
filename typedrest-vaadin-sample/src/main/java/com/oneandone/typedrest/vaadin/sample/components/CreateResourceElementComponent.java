package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.sample.forms.ResourceForm;
import com.oneandone.typedrest.vaadin.components.CreateElementComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class CreateResourceElementComponent
        extends CreateElementComponent<Resource, ResourceElement> {

    public CreateResourceElementComponent(ResourceCollection endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint));
    }
}
