package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.TriggerEndpoint;

/**
 * Indicates that {@link TriggerEndpoint#trigger()} was called.
 *
 * @param <TEndpoint> The type of endpoint that raised the event.
 */
public class TriggerEvent<TEndpoint extends TriggerEndpoint>
        extends EndpointEvent<TEndpoint> {

    /**
     * Creates a new trigger event.
     *
     * @param endpoint The endpoint that was triggered.
     */
    public TriggerEvent(TEndpoint endpoint) {
        super(endpoint);
    }
}
