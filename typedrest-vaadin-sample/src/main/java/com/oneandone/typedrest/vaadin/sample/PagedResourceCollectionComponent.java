package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.ui.*;

public class PagedResourceCollectionComponent
        extends AbstractPagedCollectionComponent<Resource, PagedResourceCollection, ResourceElement> {

    public PagedResourceCollectionComponent(PagedResourceCollection endpoint) {
        super(endpoint);
    }

    @Override
    protected Window buildCreateElementComponent() {
        return null;
    }

    @Override
    protected Window buildUpdateElementComponent(ResourceElement elementEndpoint) {
        return new UpdateResourceElementComponent(elementEndpoint);
    }
}
