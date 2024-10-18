package com.vittoriopiotti.pathgraph.callbacks;

/**
 * A callback interface for adjusting positions without parameters.
 *
 * @author vittoriopiotti
 */
public interface AdjustPositionCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        handle();
    }

    /**
     * Handles the position adjustment.
     */
    void handle();
}