package net.typedrest.vaadin.events;

import net.typedrest.ActionEndpoint;

/**
 * Indicates that {@link ActionEndpoint#trigger()} was called.
 */
public class TriggerEvent extends EndpointEvent<ActionEndpoint> {

    /**
     * Creates a new trigger event.
     *
     * @param endpoint The endpoint that was triggered.
     */
    public TriggerEvent(ActionEndpoint endpoint) {
        super(endpoint);
    }
}
