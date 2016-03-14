package com.oneandone.typedrest.vaadin.sample.views;

import com.oneandone.typedrest.vaadin.views.AbstractCollectionView;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
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
