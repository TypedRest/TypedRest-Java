package com.oneandone.typedrest.vaadin;

import java.io.Serializable;

@FunctionalInterface
public interface EntityClickListener<TEntity> extends Serializable {

    public void entityClick(TEntity entity);
}
