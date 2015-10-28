package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.ElementEndpoint;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import org.apache.http.HttpException;

/**
 * Component operating on a {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity the {@link ElementEndpoint} represents.
 */
public class ElementComponent<TEntity>
        extends AbstractComponent<ElementEndpoint<TEntity>> implements Loadable {

    @Getter
    private final BeanItemContainer<TEntity> container;

    @Getter
    protected final Grid grid = new Grid();

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint);

        container = new BeanItemContainer<>(endpoint.getEntityType());

        grid.setContainerDataSource(container);

        setCompositionRoot(grid);
    }

    @Override
    public boolean load() {
        try {
            container.addBean(endpoint.read());
            return true;
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
            return false;
        }
    }
}
