package com.oneandone.typedrest.vaadin.sample;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;

/**
 * Reports errors using {@link Notification}.
 */
public class NotificationErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        Notification.show("error", event.getThrowable().getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
    }
}
