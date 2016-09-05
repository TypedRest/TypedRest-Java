package com.oneandone.typedrest.vaadin.views;

import static com.oneandone.typedrest.ThrowableUtils.getFullMessage;
import com.oneandone.typedrest.vaadin.NotificationErrorHandler;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rx.Observable;
import rx.Observer;

/**
 * Subscribes to an {@link Observable} and consumes the elements using a Pushy
 * {@link UI}.
 */
public class UISubscriber<T> implements Observer<T> {

    private final UI ui;
    private final Consumer<T> consumer;

    /**
     * Creates a new UI subscriber. Captures {@link UI#getCurrent()}.
     *
     * @param consumer A consuming delegate called for each element in the
     * context of {@link UI#access(java.lang.Runnable)}.
     */
    public UISubscriber(Consumer<T> consumer) {
        this.ui = UI.getCurrent();
        this.consumer = consumer;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(final Throwable throwable) {
        ui.access(() -> {
            Logger.getLogger(NotificationErrorHandler.class.getName()).log(Level.WARNING, null, throwable);
            Notification.show("Error", getFullMessage(throwable), Notification.Type.WARNING_MESSAGE);
            ui.push();
        });
    }

    @Override
    public void onNext(final T element) {
        ui.access(() -> {
            consumer.accept(element);
            ui.push();
        });
    }
}
