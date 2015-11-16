package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class UpdateResourceRevisionElementComponent
        extends UpdateElementComponent<ResourceRevision> {

    protected ResourceRevisionElement endpoint;

    public UpdateResourceRevisionElementComponent(ResourceRevisionElement endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new TriggerComponent(endpoint.promote, "Promote"));
    }
}
