package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.events.TriggerEvent;
import com.vaadin.ui.*;
import org.apache.http.*;
import java.io.*;

/**
 * View component operating on a {@link ActionEndpoint}.
 */
public class ActionView extends AbstractEndpointView<ActionEndpoint> {

    protected final Button button;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param caption A caption for the triggerable action.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    public ActionView(ActionEndpoint endpoint, EventBus eventBus, String caption) {
        super(endpoint, eventBus);
        setWidthUndefined();

        setCompositionRoot(button = new Button(caption, x -> trigger()));
    }

    @Override
    protected void onLoad() {
        try {
            endpoint.probe();
        } catch (IOException | IllegalAccessException | RuntimeException ex) {
            // HTTP OPTIONS server-side implementation is optional
        }

        endpoint.isTriggerAllowed().ifPresent(this::setEnabled);
    }

    /**
     * Triggers the action.
     */
    public void trigger() {
        try {
            onTrigger();
            Notification.show(getCaption(), "Successful.", Notification.Type.TRAY_NOTIFICATION);
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
            onError(ex);
        }
    }

    /**
     * Handler for triggering the action.
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
    protected void onTrigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        endpoint.trigger();
        eventBus.post(new TriggerEvent(endpoint));
    }
}
