package com.oneandone.typedrest.sample.client.vaadin.views;

import com.oneandone.typedrest.sample.client.ResourceCollectionEndpoint;
import com.oneandone.typedrest.sample.client.ResourceEndpoint;
import com.oneandone.typedrest.sample.model.Resource;
import com.oneandone.typedrest.vaadin.views.AbstractCollectionView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.vaadin.views.ViewComponent;

public class ResourceCollectionView
        extends AbstractCollectionView<Resource, ResourceCollectionEndpoint, ResourceEndpoint> {

    public ResourceCollectionView(ResourceCollectionEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
        setCaption("Resources");
    }

    @Override
    protected ViewComponent buildElementView(ResourceEndpoint elementEndpoint) {
        return new ResourceView(elementEndpoint, eventBus);
    }

    @Override
    protected ViewComponent buildCreateElementView() {
        return new CreateResourceView(endpoint, eventBus);
    }
}
