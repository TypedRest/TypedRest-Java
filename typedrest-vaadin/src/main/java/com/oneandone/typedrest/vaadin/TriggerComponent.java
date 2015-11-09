package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.TriggerEndpoint;
import com.vaadin.ui.*;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Component operating on a {@link TriggerEndpoint}.
 */
public class TriggerComponent extends AbstractComponent<TriggerEndpoint> {

    private final Button button;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param caption A caption for the triggerable action.
     */
    public TriggerComponent(TriggerEndpoint endpoint, String caption) {
        super(endpoint);
        setCaption(caption);

        setContent(button = new Button(caption, x -> {
            try {
                endpoint.trigger();
                Notification.show(caption, "Successful.", Notification.Type.TRAY_NOTIFICATION);
            } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
                getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
            }
        }));
    }
}
