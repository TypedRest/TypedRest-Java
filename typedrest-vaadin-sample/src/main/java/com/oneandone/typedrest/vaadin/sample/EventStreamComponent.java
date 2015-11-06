package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.Event;
import com.oneandone.typedrest.vaadin.AbstractStreamComponent;
import com.vaadin.ui.Window;

public class EventStreamComponent
        extends AbstractStreamComponent<Event, StreamEndpointImpl<Event>, ElementEndpoint<Event>> {

    public EventStreamComponent(StreamEndpointImpl<com.oneandone.typedrest.sample.models.Event> endpoint) {
        super(endpoint);

        setUpdateEnabled(false);
        setCreateEnabled(false);
        setDeleteEnabled(false);
    }

    @Override
    protected Window buildCreateElementComponent() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Window buildUpdateElementComponent(ElementEndpoint<com.oneandone.typedrest.sample.models.Event> elementEndpoint) {
        throw new UnsupportedOperationException();
    }
}
