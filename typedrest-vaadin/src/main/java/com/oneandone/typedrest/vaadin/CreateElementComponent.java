package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.CollectionEndpoint;
import com.oneandone.typedrest.ElementEndpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import lombok.SneakyThrows;
import org.apache.http.HttpException;

/**
 * Component for creating a new element in a {@link CollectionEndpoint}.
 *
 * @param <TEntity> The type of entity to create.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * {@link CollectionEndpoint} provides for individual <code>TEntity</code>s.
 */
public class CreateElementComponent<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractElementComponent<TEntity, CollectionEndpoint<TEntity, TElementEndpoint>> {

    /**
     * Creates a new REST element creation component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    @SneakyThrows
    public CreateElementComponent(CollectionEndpoint<TEntity, TElementEndpoint> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);
        setCaption("New " + endpoint.getEntityType().getSimpleName());

        entityForm.setEntity(endpoint.getEntityType().getConstructor().newInstance());
    }

    /**
     * Creates a new REST element creation component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public CreateElementComponent(CollectionEndpoint<TEntity, TElementEndpoint> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        endpoint.create(entityForm.getEntity());
    }
}
