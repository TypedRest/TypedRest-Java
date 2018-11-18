package net.typedrest.vaadin;

import static net.typedrest.ThrowableUtils.getFullMessage;
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
        Notification.show("System error", getFullMessage(ex), Notification.Type.ERROR_MESSAGE);
    }
}
