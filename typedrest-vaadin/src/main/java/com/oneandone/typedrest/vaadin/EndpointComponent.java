package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.Endpoint;
import com.vaadin.ui.*;
import java.io.*;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Common base class for components operating on an {@link Endpoint}.
 *
 * @param <TEndpoint> The specific type of {@link Endpoint} to operate on.
 */
public abstract class EndpointComponent<TEndpoint extends Endpoint>
        extends CustomComponent {

    /**
     * The REST endpoint this component operates on.
     */
    protected final TEndpoint endpoint;

    /**
     * Used to send refresh notifications.
     */
    protected final EventBus eventBus;

    /**
     * Creates a new REST endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    protected EndpointComponent(TEndpoint endpoint, EventBus eventBus) {
        this.endpoint = endpoint;
        this.eventBus = eventBus;
    }

    @Override
    public void attach() {
        super.attach();
        refresh();
        eventBus.register(this);
    }

    @Override
    public void detach() {
        eventBus.unregister(this);
        super.detach();
    }

    /**
     * Reloads data from the endpoint.
     */
    public void refresh() {
        try {
            onLoad();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            onError(ex);
        }
    }

    /**
     * Handler for loading data for the endpoint.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws HttpException Other non-success status code.
     */
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
    }

    /**
     * Handler for errors reported by REST endpoints.
     *
     * @param ex The exception reported by the REST endpoint.
     */
    protected void onError(Exception ex) {
        Notification.show("Error", ex.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
    }

    private Window window;

    /**
     * Wraps the control in a window.
     *
     * @return The newly created window.
     */
    public Window asWindow() {
        if (isWindow()) {
            throw new IllegalStateException("Component can only be converted to a window once.");
        }

        this.setSizeFull();
        window = new Window(getCaption(), this);
        window.setWidth(80, Unit.PERCENTAGE);
        window.setHeight(80, Unit.PERCENTAGE);
        window.center();
        return window;
    }

    /**
     * Indicates whether this control has been wrapped in a window.
     *
     * @return <code>true</code> if this control has been wrapped in a window.
     */
    public boolean isWindow() {
        return window != null;
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        if (isWindow()) {
            window.setCaption(caption);
        }
    }

    /**
     * Closes the containing window.
     */
    public void close() {
        if (isWindow()) {
            window.close();
        }
    }
}
