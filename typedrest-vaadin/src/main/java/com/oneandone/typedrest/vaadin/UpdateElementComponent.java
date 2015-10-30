package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.ElementEndpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Component for updating an existing {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity to update.
 */
public class UpdateElementComponent<TEntity>
        extends AbstractElementComponent<TEntity, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST element updating component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public UpdateElementComponent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint, endpoint.getEntityType());
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());

        container.removeAllItems();
        container.addBean(entity);
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = container.getIdByIndex(0);
        endpoint.update(entity);
    }
}
