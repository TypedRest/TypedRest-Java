package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.components.ElementComponent;
import com.oneandone.typedrest.vaadin.components.TriggerComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class ResourceRevisionElementComponent
        extends ElementComponent<ResourceRevision> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceRevisionElement endpoint;

    public ResourceRevisionElementComponent(ResourceRevisionElement endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new TriggerComponent(endpoint.getPromote(), eventBus, "Promote"));
    }
}
