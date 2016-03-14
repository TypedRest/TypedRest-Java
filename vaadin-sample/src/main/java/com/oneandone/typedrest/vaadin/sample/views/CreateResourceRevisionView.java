package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.views.CreateElementView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.AbstractCollectionEndpoint;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class CreateResourceRevisionView
        extends CreateElementView<ResourceRevision, ResourceRevisionEndpoint> {

    public CreateResourceRevisionView(AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionEndpoint> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }
}
