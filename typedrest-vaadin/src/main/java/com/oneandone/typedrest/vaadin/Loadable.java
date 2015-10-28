package com.oneandone.typedrest.vaadin;

/**
 * A component that delay-loads external data.
 */
public interface Loadable {

    /**
     * Tells the component to load or refresh its external data.
     *
     * @return <code>true</code> if the load was successful, <code>false</code>
     * if the load failed and the failure was reported.
     */
    boolean load();
}
