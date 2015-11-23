package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.CollectionEndpoint;
import com.oneandone.typedrest.ElementEndpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.OperationNotSupportedException;
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
     * @param editor An editor component for creating entity instances.
     */
    public CreateElementComponent(CollectionEndpoint<TEntity, TElementEndpoint> endpoint, EntityEditor<TEntity> editor) {
        super(endpoint, editor);
        setCaption("New " + endpoint.getEntityType().getSimpleName());

        try {
            editor.setEntity((TEntity) endpoint.getEntityType().getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a new REST element creation component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public CreateElementComponent(CollectionEndpoint<TEntity, TElementEndpoint> endpoint) {
        this(endpoint, new DefaultEntityEditor<>(endpoint.getEntityType()));
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        endpoint.create(editor.getEntity());
    }
}
