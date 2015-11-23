package com.oneandone.typedrest.vaadin.sample.components;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.*;

public class PagedResourceCollectionComponent
        extends AbstractPagedCollectionComponent<Resource, PagedResourceCollection, ResourceElement> {

    public PagedResourceCollectionComponent(PagedResourceCollection endpoint) {
        super(endpoint);
        setCaption("Resources (paged)");
    }

    @Override
    protected Window buildElementWindow(ResourceElement elementEndpoint) {
        return new ResourceElementComponent(elementEndpoint).asWindow();
    }

    @Override
    protected Window buildCreateElementWindow() {
        return new CreateResourceElementComponent(endpoint).asWindow();
    }
}
