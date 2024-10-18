package com.vittoriopiotti.pathgraph.callbacks;

import javafx.scene.input.MouseEvent;

/**
 * A callback interface for handling node events with mouse and a character.
 *
 * @author vittoriopiotti
 */
public interface NodeCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        if (params.length == 2 && params[0] instanceof MouseEvent && params[1] instanceof Character) {
            handle((MouseEvent) params[0], (Character) params[1]);
        }
    }

    /**
     * Handles the node event.
     *
     * @param event the mouse event
     * @param node  the character representing the node
     */
    void handle(MouseEvent event, Character node);
}
