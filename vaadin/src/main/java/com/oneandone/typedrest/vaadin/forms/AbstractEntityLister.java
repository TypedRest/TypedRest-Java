package com.oneandone.typedrest.vaadin.forms;

import static com.oneandone.typedrest.BeanUtils.*;
import com.oneandone.typedrest.ListerHidden;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import java.util.*;

/**
 * Base class for building entity lister implementations.
 *
 * @param <TEntity> The type of entities the list shows.
 */
public abstract class AbstractEntityLister<TEntity>
        extends CustomComponent implements EntityLister<TEntity> {

    protected final Class<TEntity> entityType;
    protected final BeanItemContainer<TEntity> container;

    /**
     * Creates a new entity lister.
     *
     * @param entityType The type of entities the list shows.
     */
    protected AbstractEntityLister(Class<TEntity> entityType) {
        this.entityType = entityType;

        this.container = new BeanItemContainer<>(entityType);
        getPropertiesWithAnnotation(entityType, ListerHidden.class)
                .forEach(x -> container.removeContainerProperty(x.getName()));
    }

    @Override
    public void addEntities(Collection<TEntity> entities) {
        container.addAll(entities);
    }

    @Override
    public void clearEntities() {
        container.removeAllItems();
    }

    @Override
    public int entityCount() {
        return container.getItemIds().size();
    }

    private final Collection<EntityClickListener<TEntity>> clickListeners = new LinkedList<>();

    @Override
    public void addEntityClickListener(EntityClickListener<TEntity> listener) {
        clickListeners.add(listener);
    }

    protected void onClick(TEntity entity) {
        clickListeners.forEach(x -> x.entityClick(entity));
    }
}
