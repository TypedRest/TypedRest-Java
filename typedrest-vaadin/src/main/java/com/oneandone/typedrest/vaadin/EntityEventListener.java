package com.oneandone.typedrest.vaadin;

/**
 * 
 * 
 * @param <TEntity>
 */
public interface EntityEventListener<TEntity> {
    void event(TEntity element);
}
