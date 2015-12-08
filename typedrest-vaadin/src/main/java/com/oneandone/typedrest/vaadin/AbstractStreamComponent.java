package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.vaadin.annotations.Push;
import com.vaadin.ui.Notification;
import com.vaadin.ui.*;
import rx.*;

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

        setOpenElementEnabled(false);
        setCreateEnabled(false);
        setDeleteEnabled(false);
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

    private Subscription subscription;

    @Override
    public void attach() {
        super.attach();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = endpoint.getObservable().subscribe(new EntityObserver(UI.getCurrent()));
    }

    @Override
    public void detach() {
        subscription.unsubscribe();
        super.detach();
    }

    private final class EntityObserver implements Observer<TEntity> {

        private final UI ui;

        public EntityObserver(UI ui) {
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
                ui.getErrorHandler().error(new com.vaadin.server.ErrorEvent(throwable));
                ui.push();
            });
        }

        @Override
        public void onNext(final TEntity entity) {
            ui.access(() -> {
                lister.addEntity(entity);
                lister.scrollToEnd();
                ui.push();
            });
        }
    }
}
