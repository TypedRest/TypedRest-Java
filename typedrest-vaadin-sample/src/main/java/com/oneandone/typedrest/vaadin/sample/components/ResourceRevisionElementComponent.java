package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;

public class ResourceRevisionElementComponent
        extends ElementComponent<ResourceRevision> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceRevisionElement endpoint;

    public ResourceRevisionElementComponent(ResourceRevisionElement endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new TriggerComponent(endpoint.promote, "Promote"));
    }
}
