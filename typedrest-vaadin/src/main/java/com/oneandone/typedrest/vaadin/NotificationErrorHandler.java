package com.oneandone.typedrest.vaadin;

import static com.google.gwt.thirdparty.guava.common.base.Throwables.getRootCause;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;
import java.util.logging.*;

/**
 * Reports errors using {@link Notification} and {@link Logger}.
 */
public class NotificationErrorHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        Throwable ex = event.getThrowable();
        Logger.getLogger(NotificationErrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        Notification.show("System error", getRootCause(ex).getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
    }
}
