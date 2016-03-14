package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.sample.forms.ResourceForm;
import com.oneandone.typedrest.vaadin.views.ElementView;
import com.oneandone.typedrest.vaadin.views.StreamView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.vaadin.ui.Button;

public class ResourceView
        extends ElementView<Resource> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Covariance
    protected ResourceEndpoint endpoint;

    public ResourceView(ResourceEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus, new ResourceForm(endpoint.getResources()));
        this.endpoint = endpoint;

        buttonsLayout.addComponent(new Button("Events", x
                -> open(new StreamView<>(endpoint.getEvents(), eventBus))));

        masterLayout.addComponent(new ResourceRevisionCollectionView(endpoint.getRevisions(), eventBus));
    }
}
