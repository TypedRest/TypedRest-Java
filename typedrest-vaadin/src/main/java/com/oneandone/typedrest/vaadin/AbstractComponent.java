package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

/**
 * Component operating on a {@link Endpoint}.
 *
 * @param <TEndpoint> The specific type of {@link Endpoint} to operate on.
 */
public abstract class AbstractComponent<TEndpoint extends Endpoint>
        extends CustomComponent {

    /**
     * The REST endpoint this component operates on.
     */
    protected final TEndpoint endpoint;

    /**
     * Creates a new REST endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    protected AbstractComponent(TEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        ErrorHandler handler = super.getErrorHandler();
        return (handler == null) ? UI.getCurrent().getErrorHandler() : handler;
    }
}
