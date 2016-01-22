package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.views.ElementView;
import com.oneandone.typedrest.vaadin.views.ConfirmationTriggerView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;

public class ResourceRevisionElementView
        extends ElementView<ResourceRevision> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceRevisionElement endpoint;

    public ResourceRevisionElementView(ResourceRevisionElement endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new ConfirmationTriggerView(endpoint.getPromote(), eventBus, "Promote", "Are you sure?"));
    }
}
