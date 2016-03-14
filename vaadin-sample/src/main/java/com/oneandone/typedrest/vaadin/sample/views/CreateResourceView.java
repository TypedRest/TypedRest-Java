package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.sample.forms.ResourceForm;
import com.oneandone.typedrest.vaadin.views.CreateElementView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class CreateResourceView
        extends CreateElementView<Resource, ResourceEndpoint> {

    public CreateResourceView(ResourceCollectionEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint));
    }
}
