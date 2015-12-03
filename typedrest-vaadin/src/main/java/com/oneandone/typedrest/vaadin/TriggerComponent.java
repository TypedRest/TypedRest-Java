package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Component operating on a {@link TriggerEndpoint}.
 */
public class TriggerComponent extends AbstractComponent<TriggerEndpoint> {

    protected final Button button;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param caption A caption for the triggerable action.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    public TriggerComponent(TriggerEndpoint endpoint, String caption) {
        super(endpoint);

        setCompositionRoot(button = new Button(caption, x -> {
            try {
                onTrigger();
                refreshWatchers();
                Notification.show(caption, "Successful.", Notification.Type.TRAY_NOTIFICATION);
            } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
                onError(ex);
            }
        }));
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
     * @throws HttpException Other non-success status code.
     */
    protected void onTrigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        endpoint.trigger();
    }
}
