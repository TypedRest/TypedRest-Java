package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.views.AbstractEntryView;
import com.oneandone.typedrest.vaadin.views.CollectionView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

public class SampleEntryView
        extends AbstractEntryView<SampleEntryEndpoint> {

    public SampleEntryView(SampleEntryEndpoint endpoint) {
        super(endpoint);
        setCaption("TypedRest Sample");
    }

    @Override
    protected Component buildRoot() {
        return new TabSheet(
                new ResourceCollectionView(endpoint.getResources(), eventBus),
                new CollectionView<>(endpoint.getTargets(), eventBus));
    }
}
