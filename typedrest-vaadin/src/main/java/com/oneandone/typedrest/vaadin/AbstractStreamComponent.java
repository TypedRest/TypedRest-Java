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

    protected Observer<TEntity> observer = new EntityObserver<>();
    protected Observable<TEntity> observable;
    protected Subscription currentSubscription;

    /**
     * Creates a new REST stream component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public AbstractStreamComponent(TEndpoint endpoint) {
        super(endpoint);
        setUpdateEnabled(false);
        setCreateEnabled(false);
        setDeleteEnabled(false);
    }

    @Override
    protected void onLoad() throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        observable = endpoint.getObservable();
        observable.subscribe(observer);
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
        this.observer = observer;
        if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
            currentSubscription.unsubscribe();
        }
        if (observable != null) {
            observable.subscribe(observer);
        }
    }

    private class EntityObserver<T> implements Observer<T> {

        @Override
        public void onCompleted() {
            Notification.show("Done", "No more Data available.", Notification.Type.TRAY_NOTIFICATION);
        }

        @Override
        public void onError(Throwable throwable) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(throwable));
        }

        @Override
        public void onNext(T tEntity) {
            UI.getCurrent().access(() -> {
                grid.getContainerDataSource().addItem(tEntity);
                UI.getCurrent().push();
            });
        }
    }
}
