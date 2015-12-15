package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.google.gwt.thirdparty.guava.common.eventbus.Subscribe;
import com.oneandone.typedrest.Endpoint;
import com.oneandone.typedrest.vaadin.events.EndpointEvent;
import com.vaadin.ui.*;
import java.io.*;
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

        setSizeFull();
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
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException ex) {
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
     * @throws RuntimeException Other non-success status code.
     */
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
    }

    /**
     * Handler for errors reported by REST endpoints.
     *
     * @param ex The exception reported by the REST endpoint.
     */
    protected void onError(Exception ex) {
        Notification.show("Error", ex.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
    }

    /**
     * Opens a child component as a {@link Window}.
     *
     * @param component The child component to open.
     */
    protected void open(EndpointComponent<?> component) {
        getUI().addWindow(component.asWindow());
    }

    private Window containingWindow;

    /**
     * Wraps the control in a {@link Window}.
     *
     * @return The newly created window.
     */
    private Window asWindow() {
        if (isContained()) {
            throw new IllegalStateException("Component can only be wrapped once.");
        }

        containingWindow = new Window(getCaption(), this);
        containingWindow.setWidth(80, Unit.PERCENTAGE);
        containingWindow.setHeight(80, Unit.PERCENTAGE);
        containingWindow.center();
        return containingWindow;
    }

    /**
     * Indicates whether this control has been wrapped in a container.
     *
     * @return <code>true</code> if this control has been wrapped in a
     * container.
     */
    public boolean isContained() {
        return containingWindow != null;
    }

    /**
     * Closes the containing {@link Window}.
     */
    protected void close() {
        if (containingWindow != null) {
            containingWindow.close();
        }
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        if (containingWindow != null) {
            containingWindow.setCaption(caption);
        }
    }

    @Subscribe
    public void handle(EndpointEvent<?> message) {
        if (message.getEndpoint().getNotifyTargets().contains(endpoint.getUri())) {
            refresh();
        }
    }
}
