package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import org.apache.http.HttpException;

/**
 * Base class for building components operating on an
 * {@link CollectionEndpoint}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link CollectionEndpoint} to operate
 * on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractCollectionComponent<TEntity, TEndpoint extends CollectionEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractComponent<TEndpoint> implements Loadable {

    private final BeanItemContainer<TEntity> container;

    @Getter
    protected final Grid grid = new Grid();

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    protected AbstractCollectionComponent(TEndpoint endpoint) {
        super(endpoint);

        container = new BeanItemContainer<>(endpoint.getEntityType());

        grid.setContainerDataSource(container);
        grid.addItemClickListener(event -> {
            elementClickListeners.forEach(listener -> listener.event((TEntity) event.getItem()));
        });

        setCompositionRoot(grid);
    }

    private final Collection<EntityEventListener<TEntity>> elementClickListeners = new LinkedHashSet<>();

    public void addElementClickListener(EntityEventListener<TEntity> listener) {
        elementClickListeners.add(listener);
    }

    public void removeElementClickListener(EntityEventListener<TEntity> listener) {
        elementClickListeners.remove(listener);
    }

    @Override
    public boolean load() {
        try {
            container.addAll(endpoint.readAll());
            return true;
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
            return false;
        }
    }
}
