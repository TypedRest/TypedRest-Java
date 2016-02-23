package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.sample.forms.ResourceForm;
import com.oneandone.typedrest.vaadin.views.CreateElementView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class CreateResourceElementView
        extends CreateElementView<Resource, ResourceElement> {

    public CreateResourceElementView(ResourceCollection endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint));
    }
}
