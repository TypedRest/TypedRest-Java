package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.TriggerEndpoint;

/**
 * Indicates that {@link TriggerEndpoint#trigger()} was called.
 */
public class TriggerEvent extends EndpointEvent<TriggerEndpoint> {

    /**
     * Creates a new trigger event.
     *
     * @param endpoint The endpoint that was triggered.
     */
    public TriggerEvent(TriggerEndpoint endpoint) {
        super(endpoint);
    }
}
