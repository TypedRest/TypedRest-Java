package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.views.CreateElementView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.AbstractCollectionEndpoint;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class CreateResourceRevisionElementView
        extends CreateElementView<ResourceRevision, ResourceRevisionElement> {

    public CreateResourceRevisionElementView(AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionElement> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }
}
