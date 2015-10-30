package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.CollectionEndpoint;
import com.oneandone.typedrest.ElementEndpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Component for creating a new {@link ElementEndpoint}.
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
     */
    public CreateElementComponent(CollectionEndpoint<TEntity, TElementEndpoint> endpoint) {
        super(endpoint, endpoint.getEntityType());
        setCaption("New " + endpoint.getEntityType().getSimpleName());
        grid.setEditorEnabled(true);

        createEmptyEntity();
    }

    @SuppressWarnings("unchecked")
    private void createEmptyEntity() {
        try {
            container.addBean((TEntity) endpoint.getEntityType().getConstructor().newInstance());
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = container.getIdByIndex(0);
        endpoint.create(entity);
    }
}
