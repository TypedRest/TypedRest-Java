package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.vaadin.sample.forms.ResourceForm;
import com.oneandone.typedrest.vaadin.components.ElementComponent;
import com.oneandone.typedrest.vaadin.components.StreamComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.Button;

public class ResourceElementComponent
        extends ElementComponent<Resource> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceElement endpoint;

    public ResourceElementComponent(ResourceElement endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint.parentResources));
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new Button("Events", x
                -> open(new StreamComponent<>(endpoint.events, eventBus))));

        masterLayout.addComponent(new ResourceRevisionCollectionComponent(endpoint.revisions, eventBus));
    }
}
