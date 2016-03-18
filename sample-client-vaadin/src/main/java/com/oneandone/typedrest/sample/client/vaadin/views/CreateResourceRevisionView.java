package com.oneandone.typedrest.sample.client.vaadin.views;

import com.oneandone.typedrest.sample.client.ResourceRevisionEndpoint;
import com.oneandone.typedrest.sample.model.ResourceRevision;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.AbstractCollectionEndpoint;
import com.oneandone.typedrest.vaadin.views.CreateElementView;

public class CreateResourceRevisionView
        extends CreateElementView<ResourceRevision, ResourceRevisionEndpoint> {

    public CreateResourceRevisionView(AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionEndpoint> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }
}
