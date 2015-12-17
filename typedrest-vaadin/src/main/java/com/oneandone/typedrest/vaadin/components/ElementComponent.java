package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.ElementEndpoint;
import com.oneandone.typedrest.vaadin.events.ElementUpdatedEvent;
import com.oneandone.typedrest.vaadin.forms.DefaultEntityForm;
import com.oneandone.typedrest.vaadin.forms.EntityForm;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * Component for showing or updating an existing element represented by an
 * {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity to represent.
 */
public class ElementComponent<TEntity>
        extends AbstractElementComponent<TEntity, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);
    }

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());
        entityForm.setEntity(entity);
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        endpoint.update(entityForm.getEntity());
        eventBus.post(new ElementUpdatedEvent<>(endpoint));
    }
}