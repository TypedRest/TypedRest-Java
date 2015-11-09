package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.Button;

public class UpdateResourceRevisionElementComponent
        extends UpdateElementComponent<ResourceRevision> {

    protected ResourceRevisionElement endpoint;

    public UpdateResourceRevisionElementComponent(ResourceRevisionElement endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new Button("Promote",
                x -> getUI().addWindow(new TriggerComponent(endpoint.promote, "Promote"))));
    }
}
