package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.DefaultEntityLister;
import com.oneandone.typedrest.vaadin.forms.EntityLister;
import com.vaadin.annotations.Push;
import com.vaadin.ui.Notification;
import com.vaadin.ui.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.*;
import rx.util.async.StoppableObservable;

/**
 * Base class for building components operating on an {@link StreamEndpoint}.
 *
 * To enable server-push (real-time actualization of the web-ui), please make
 * sure to have your {@link UI} annotated with {@link Push}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link StreamEndpoint} to operate on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractStreamComponent<TEntity, TEndpoint extends StreamEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionComponent<TEntity, TEndpoint, TElementEndpoint> {

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    protected AbstractStreamComponent(TEndpoint endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
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
    protected AbstractStreamComponent(TEndpoint endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityLister<>(endpoint.getEntityType()));
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
                Notification.show("Error", throwable.getLocalizedMessage(), Notification.Type.WARNING_MESSAGE);
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
