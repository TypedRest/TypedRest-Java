package com.oneandone.typedrest.vaadin;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reports errors using {@link Notification}.
 */
public class NotificationErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        Notification.show("error", event.getThrowable().getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
        Logger.getLogger(NotificationErrorHandler.class.getName()).log(Level.SEVERE, null, event.getThrowable());
    }
}
