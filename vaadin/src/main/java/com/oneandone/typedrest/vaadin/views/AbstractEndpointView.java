package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.google.gwt.thirdparty.guava.common.eventbus.Subscribe;
import com.oneandone.typedrest.Endpoint;
import com.oneandone.typedrest.vaadin.events.EndpointEvent;
import com.vaadin.ui.*;
import java.io.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.*;

/**
 * Base class for building view components operating on an {@link Endpoint}.
 *
 * @param <TEndpoint> The specific type of {@link Endpoint} to operate on.
 */
public abstract class AbstractEndpointView<TEndpoint extends Endpoint>
        extends ViewComponent {

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
    protected AbstractEndpointView(TEndpoint endpoint, EventBus eventBus) {
        this.endpoint = endpoint;
        this.eventBus = eventBus;

        setSizeFull();
    }

    boolean eventBusRegistered;

    @Override
    public void attach() {
        super.attach();
        refresh();

        if (!eventBusRegistered) {
            eventBus.register(this);
            eventBusRegistered = true;
        }
    }

    @Override
    public void detach() {
        if (eventBusRegistered) {
            eventBus.unregister(this);
            eventBusRegistered = false;
        }

        super.detach();
    }

    /**
     * Reloads data from the endpoint.
     */
    public void refresh() {
        try {
            onLoad();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
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
     * The Link relation type used by the server to send refresh notifications.
     */
    @Getter
    @Setter
    private String notifyRel = "notify";

    @Subscribe
    public void handle(EndpointEvent<?> message) {
        if (message.getEndpoint().getLinks(notifyRel).contains(endpoint.getUri())) {
            refresh();
        }
    }
}
