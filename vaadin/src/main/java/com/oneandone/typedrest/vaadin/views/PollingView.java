package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.AutoEntityForm;
import com.oneandone.typedrest.vaadin.forms.EntityForm;
import com.vaadin.data.Validator;
import java.io.FileNotFoundException;
import java.io.IOException;
import rx.util.async.StoppableObservable;

/**
 * View component operating on a {@link PollingEndpoint}.
 *
 * @param <TEntity> The type of entity the {@link PollingEndpoint} represents.
 */
public class PollingView<TEntity>
        extends AbstractElementView<TEntity, PollingEndpoint<TEntity>> {

    /**
     * Creates a new REST polling component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing entity instances.
     */
    public PollingView(PollingEndpoint<TEntity> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);

        saveButton.setVisible(false);
        setReadOnly(true);
        entityForm.setReadOnly(false);
    }

    /**
     * Creates a new REST polling component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public PollingView(PollingEndpoint<TEntity> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new AutoEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad() {
        // Do nothing here, loading happens async
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException, Validator.InvalidValueException {
        throw new UnsupportedOperationException();
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
        observable = endpoint.getObservable(2);
        observable.subscribe(new UISubscriber<>(entityForm::setEntity));
    }

    private void stopStreaming() {
        if (observable != null) {
            observable.unsubscribe();
            observable = null;
        }
    }
}
