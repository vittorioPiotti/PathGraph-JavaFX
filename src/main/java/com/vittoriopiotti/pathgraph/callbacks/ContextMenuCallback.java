package com.vittoriopiotti.pathgraph.callbacks;



/**
 * A callback interface for handling context menu events.
 *
 * @author vittoriopiotti
 */
public interface ContextMenuCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        handle();
    }

    /**
     * Handles the context menu event.
     */
    void handle();
}