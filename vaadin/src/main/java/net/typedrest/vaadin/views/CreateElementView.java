package net.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import net.typedrest.CollectionEndpoint;
import net.typedrest.ElementEndpoint;
import net.typedrest.GenericCollectionEndpoint;
import net.typedrest.vaadin.events.ElementCreatedEvent;
import net.typedrest.vaadin.forms.AutoEntityForm;
import net.typedrest.vaadin.forms.EntityForm;
import com.vaadin.data.Validator;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.SneakyThrows;

/**
 * Component for creating a new element in a {@link GenericCollectionEndpoint}.
 *
 * Use the more constrained {@link CreateElementView} when possible.
 *
 * @param <TEntity> The type of entity to create.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * {@link CollectionEndpoint} provides for individual <code>TEntity</code>s.
 */
public class CreateElementView<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractElementView<TEntity, GenericCollectionEndpoint<TEntity, TElementEndpoint>> {

    /**
     * Creates a new REST element creation component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    @SneakyThrows
    public CreateElementView(GenericCollectionEndpoint<TEntity, TElementEndpoint> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);
        setCaption("New " + endpoint.getEntityType().getSimpleName());

        entityForm.setEntity(endpoint.getEntityType().getConstructor().newInstance());
    }

    /**
     * Creates a new REST element creation component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    public CreateElementView(GenericCollectionEndpoint<TEntity, TElementEndpoint> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new AutoEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException, Validator.InvalidValueException {
        TElementEndpoint newEndpoint = endpoint.create(entityForm.getEntity());
        eventBus.post(new ElementCreatedEvent<>(newEndpoint));
    }
}
