package com.vittoriopiotti.pathgraph.callbacks;

import javafx.scene.input.MouseEvent;


/**
 * A callback interface for handling mouse events.
 *
 * @author vittoriopiotti
 */
public interface BackgroundCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        if (params.length > 0 && params[0] instanceof MouseEvent) {
            handle((MouseEvent) params[0]);
        }
    }

    /**
     * Handles a mouse event.
     *
     * @param event the mouse event
     */
    void handle(MouseEvent event);
}