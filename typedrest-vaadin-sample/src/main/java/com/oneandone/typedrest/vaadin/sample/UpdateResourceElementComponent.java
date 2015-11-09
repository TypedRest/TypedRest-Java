package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.Button;

public class UpdateResourceElementComponent
        extends UpdateElementComponent<Resource> {

    protected ResourceElement endpoint;

    public UpdateResourceElementComponent(ResourceElement endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new Button("Revisions", x
                -> getUI().addWindow(new ResourceRevisionCollectionComponent(endpoint.revisions))));
        buttonsLayout.addComponent(new Button("Events", x
                -> getUI().addWindow(new StreamComponent<>(endpoint.events))));
    }
}
