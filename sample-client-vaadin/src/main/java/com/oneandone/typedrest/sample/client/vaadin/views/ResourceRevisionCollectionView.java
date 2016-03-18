package com.oneandone.typedrest.sample.client.vaadin.views;

import com.oneandone.typedrest.sample.client.ResourceRevisionEndpoint;
import com.oneandone.typedrest.sample.client.ResourceRevisionCollectionEndpoint;
import com.oneandone.typedrest.sample.model.ResourceRevision;
import com.oneandone.typedrest.vaadin.views.AbstractCollectionView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
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
