package com.oneandone.typedrest.sample.client.vaadin.views;

import com.oneandone.typedrest.sample.client.ResourceRevisionEndpoint;
import com.oneandone.typedrest.sample.model.ResourceRevision;
import com.oneandone.typedrest.vaadin.views.ElementView;
import com.oneandone.typedrest.vaadin.views.ConfirmationActionView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;

public class ResourceRevisionView
        extends ElementView<ResourceRevision> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceRevisionEndpoint endpoint;

    public ResourceRevisionView(ResourceRevisionEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new ConfirmationActionView(endpoint.getPromote(), eventBus, "Promote", "Are you sure?"));
    }
}
