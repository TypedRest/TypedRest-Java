package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import static com.oneandone.typedrest.ThrowableUtils.getFullMessage;
import com.oneandone.typedrest.vaadin.NotificationErrorHandler;
import com.oneandone.typedrest.vaadin.forms.AutoEntityLister;
import com.oneandone.typedrest.vaadin.forms.EntityLister;
import com.vaadin.annotations.Push;
import com.vaadin.ui.Notification;
import com.vaadin.ui.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rx.*;
import rx.util.async.StoppableObservable;

/**
 * Base class for building view components operating on a
 * {@link GenericStreamEndpoint}.
 *
 * To enable server-push (real-time actualization of the web-ui), please make
 * sure to have your {@link UI} annotated with {@link Push}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link GenericStreamEndpoint} to operate on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractStreamView<TEntity, TEndpoint extends GenericStreamEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionView<TEntity, TEndpoint, TElementEndpoint> {

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    protected AbstractStreamView(TEndpoint endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
        setCreateEnabled(false);
        refreshButton.setVisible(false);
    }

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    protected AbstractStreamView(TEndpoint endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new AutoEntityLister<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad() {
        // Do nothing here, loading happens async
    }

    private boolean streamingEnabled = true;

    public void setStreamingEnabled(boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
        if (isAttached()) {
            if (streamingEnabled) {
                startStreaming();
            } else {
                stopStreaming();
            }
        }
    }

    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }

    @Override
    public void attach() {
        super.attach();
        if (streamingEnabled) {
            startStreaming();
        }
    }

    @Override
    public void detach() {
        stopStreaming();
        super.detach();
    }

    private StoppableObservable<TEntity> observable;

    private void startStreaming() {
        stopStreaming();
        observable = endpoint.getObservable(lister.entityCount());
        observable
                .buffer(1, TimeUnit.SECONDS).filter(x -> !x.isEmpty())
                .subscribe(new EntitySubscriber(UI.getCurrent()));
    }

    private void stopStreaming() {
        if (observable != null) {
            observable.unsubscribe();
            observable = null;
        }
    }

    private final class EntitySubscriber implements Observer<List<TEntity>> {

        private final UI ui;

        public EntitySubscriber(UI ui) {
            this.ui = ui;
        }

        @Override
        public void onCompleted() {
            ui.access(() -> {
                Notification.show("Done", "No more data available.", Notification.Type.TRAY_NOTIFICATION);
                ui.push();
            });
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
        public void onNext(final List<TEntity> entities) {
            ui.access(() -> {
                lister.addEntities(entities);
                lister.scrollToEnd();
                ui.push();
            });
        }
    }
}
