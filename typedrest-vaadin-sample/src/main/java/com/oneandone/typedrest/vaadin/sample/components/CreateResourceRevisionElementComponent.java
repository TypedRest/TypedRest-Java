package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.components.CreateElementComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.AbstractCollectionEndpoint;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class CreateResourceRevisionElementComponent
        extends CreateElementComponent<ResourceRevision, ResourceRevisionElement> {

    public CreateResourceRevisionElementComponent(AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionElement> endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }
}
