package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.AutoEntityLister;
import com.oneandone.typedrest.vaadin.forms.EntityLister;
import com.vaadin.annotations.Push;
import com.vaadin.ui.*;
import java.util.concurrent.TimeUnit;
import rx.util.async.StoppableObservable;

/**
 * Base class for building view components operating on a
 * {@link GenericStreamEndpoint}.
 *
 * To enable server-push (real-time actualization of the web-ui), please make
 * sure to have your {@link UI} annotated with {@link Push}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link GenericStreamEndpoint} to
 * operate on.
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
                .subscribe(new UISubscriber<>(x -> {
                    lister.addEntities(x);
                    lister.scrollToEnd();
                }));
    }

    private void stopStreaming() {
        if (observable != null) {
            observable.unsubscribe();
            observable = null;
        }
    }
}
