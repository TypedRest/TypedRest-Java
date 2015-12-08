package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.ElementEndpoint;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Component for showing or updating an existing element represented by an
 * {@link ElementEndpoint}. Asks a confirmation question before saving changes.
 *
 * @param <TEntity> The type of entity to represent.
 */
public class ConfirmationElementComponent<TEntity> extends ElementComponent<TEntity> {

    private final String question;

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     * @param question A question to show the user asking whether to actually
     * save the changes.
     */
    public ConfirmationElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm, String question) {
        super(endpoint, eventBus, entityForm);
        this.question = question;
    }

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param question A question to show the user asking whether to actually
     * save the changes.
     */
    public ConfirmationElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus, String question) {
        super(endpoint, eventBus);
        this.question = question;
    }

    @Override
    protected void save() {
        ConfirmDialog.show(getUI(), question, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                super.save();
            }
        });
    }
}
