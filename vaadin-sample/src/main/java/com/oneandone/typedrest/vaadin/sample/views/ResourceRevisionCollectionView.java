package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.views.AbstractCollectionView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.views.ViewComponent;

public class ResourceRevisionCollectionView
        extends AbstractCollectionView<ResourceRevision, ResourceRevisionCollectionEndpoint, ResourceRevisionEndpoint> {

    public ResourceRevisionCollectionView(ResourceRevisionCollectionEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        setCaption("Revisions");
    }

    @Override
    protected ViewComponent buildElementView(ResourceRevisionEndpoint elementEndpoint) {
        return new ResourceRevisionView(elementEndpoint, eventBus);
    }

    @Override
    protected ViewComponent buildCreateElementView() {
        return new CreateResourceRevisionView(endpoint, eventBus);
    }
}
