package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import org.apache.http.*;
import javax.naming.*;
import java.io.*;

/**
 * Component operating on a {@link TriggerEndpoint}.
 */
public class TriggerComponent extends EndpointComponent<TriggerEndpoint> {

    protected final Button button;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param caption A caption for the triggerable action.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    public TriggerComponent(TriggerEndpoint endpoint, EventBus eventBus, String caption) {
        super(endpoint, eventBus);
        setWidthUndefined();

        setCompositionRoot(button = new Button(caption, x -> trigger()));
    }

    /**
     * Triggers the action.
     */
    protected void trigger() {
        try {
            onTrigger();
            Notification.show(getCaption(), "Successful.", Notification.Type.TRAY_NOTIFICATION);
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException ex) {
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
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    protected void onTrigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        endpoint.trigger();
        eventBus.post(endpoint);
    }
}
