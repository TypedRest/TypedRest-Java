package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.TriggerEndpoint;
import com.vaadin.ui.*;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import org.apache.http.HttpException;

/**
 * Component operating on a {@link TriggerEndpoint}.
 */
public class TriggerComponent extends AbstractComponent<TriggerEndpoint> {

    @Getter
    private final Button button;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param caption The caption of the button shown inside the control.
     */
    public TriggerComponent(TriggerEndpoint endpoint, String caption) {
        super(endpoint);

        button = new Button(caption);
        button.addClickListener(event -> {
            try {
                endpoint.trigger();
            } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
                getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
            }
        });
        setCompositionRoot(button);
    }
}
