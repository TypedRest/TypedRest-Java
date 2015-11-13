package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.annotations.Push;
import com.vaadin.ui.Notification;
import com.vaadin.ui.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;
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

    protected Observable<TEntity> observable;
    protected Subscription currentSubscription;

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param lister A component for listing entity instances.
     */
    protected AbstractStreamComponent(TEndpoint endpoint, EntityLister<TEntity> lister) {
        super(endpoint, lister);

        setUpdateEnabled(false);
        setCreateEnabled(false);
        setDeleteEnabled(false);
    }

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    protected AbstractStreamComponent(TEndpoint endpoint) {
        this(endpoint, new DefaultEntityLister<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad() throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        setObserver(new EntityObserver(UI.getCurrent()));
    }

    /**
     * Unsubscribes the current observer.
     */
    public void stopStreaming() {
        currentSubscription.unsubscribe();
    }

    /**
     * Sets the used {@link Observer} to the given one. This replaces the
     * {@link Observer} used so far and can therefore be executed while
     * observing.
     *
     * @param observer The new observer to listen on new <code>TEntity</code>s
     * incoming.
     */
    public void setObserver(Observer<TEntity> observer) {
        if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
            currentSubscription.unsubscribe();
        }

        endpoint.getObservable().subscribe(observer);
    }

    private final class EntityObserver implements Observer<TEntity> {

        private final UI ui;

        public EntityObserver(UI ui) {
            this.ui = ui;
        }

        @Override
        public void onCompleted() {
            ui.access(() -> {
                Notification.show("Done", "No more Data available.", Notification.Type.TRAY_NOTIFICATION);
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
                ui.push();
            });
        }
    }
}
