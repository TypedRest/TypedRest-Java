package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.AutoEntityForm;
import com.oneandone.typedrest.vaadin.forms.EntityForm;
import com.vaadin.data.Validator;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.*;
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

    private boolean pollingEnabled = true;

    public void setPollingEnabled(boolean streamingEnabled) {
        this.pollingEnabled = streamingEnabled;
        if (isAttached()) {
            if (streamingEnabled) {
                startPolling();
            } else {
                stopPolling();
            }
        }
    }

    public boolean isPollingEnabled() {
        return pollingEnabled;
    }

    @Override
    public void attach() {
        super.attach();
        if (pollingEnabled) {
            startPolling();
        }
    }

    @Override
    public void detach() {
        stopPolling();
        super.detach();
    }

    /**
     * The interval in milliseconds in which to send requests to the server.
     */
    @Getter
    @Setter
    private Integer pollingInterval = 4000;

    private StoppableObservable<TEntity> observable;

    private void startPolling() {
        stopPolling();
        observable = endpoint.getObservable(pollingInterval);
        observable.subscribe(new UISubscriber<>(entityForm::setEntity));
    }

    private void stopPolling() {
        if (observable != null) {
            observable.unsubscribe();
            observable = null;
        }
    }
}
