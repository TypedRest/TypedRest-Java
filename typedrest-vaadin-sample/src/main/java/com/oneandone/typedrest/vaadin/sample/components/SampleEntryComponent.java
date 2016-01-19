package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.components.AbstractEntryComponent;
import com.oneandone.typedrest.vaadin.components.CollectionComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

public class SampleEntryComponent
        extends AbstractEntryComponent<SampleEntryEndpoint> {

    public SampleEntryComponent(SampleEntryEndpoint endpoint) {
        super(endpoint);
        setCaption("TypedRest Sample");
    }

    @Override
    protected Component buildRoot() {
        return new TabSheet(
                new ResourceCollectionComponent(endpoint.getResources(), eventBus),
                new CollectionComponent<>(endpoint.getTargets(), eventBus));
    }
}
