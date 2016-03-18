package com.oneandone.typedrest.sample.client.vaadin.views;

import com.oneandone.typedrest.sample.client.ResourceCollectionEndpoint;
import com.oneandone.typedrest.sample.client.ResourceEndpoint;
import com.oneandone.typedrest.sample.model.Resource;
import com.oneandone.typedrest.sample.client.vaadin.forms.ResourceForm;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.vaadin.views.CreateElementView;

public class CreateResourceView
        extends CreateElementView<Resource, ResourceEndpoint> {

    public CreateResourceView(ResourceCollectionEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint));
    }
}
