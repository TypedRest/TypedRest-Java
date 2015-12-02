package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.ElementEndpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Component for showing or updating an existing element represented by a
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
     * @param entityForm A component for viewing/modifying entity instances.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint, EntityForm<TEntity> entityForm) {
        super(endpoint, entityForm);
    }

    /**
     * Creates a new REST element updating component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint) {
        this(endpoint, new DefaultEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());
        entityForm.setEntity(entity);
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        endpoint.update(entityForm.getEntity());
    }
}
