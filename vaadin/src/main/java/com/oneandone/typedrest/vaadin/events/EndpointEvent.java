package com.oneandone.typedrest.vaadin.events;

import com.google.common.eventbus.EventBus;
import com.oneandone.typedrest.Endpoint;
import lombok.Getter;

/**
 * An event for the {@link EventBus} that references an {@link Endpoint}.
 *
 * @param <TEndpoint> The type of endpoint that raised the event.
 */
public abstract class EndpointEvent<TEndpoint extends Endpoint> {

    /**
     * The endpoint that raised the event.
     */
    @Getter
    private final TEndpoint endpoint;

    /**
     * Creates a new endpoint event.
     *
     * @param endpoint The endpoint that raised the event.
     */
    protected EndpointEvent(TEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
