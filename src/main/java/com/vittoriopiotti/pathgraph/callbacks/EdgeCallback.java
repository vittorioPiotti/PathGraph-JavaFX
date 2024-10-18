package com.vittoriopiotti.pathgraph.callbacks;

import javafx.scene.input.MouseEvent;

/**
 * A callback interface for handling edge events with mouse and characters.
 *
 * @author vittoriopiotti
 */
public interface EdgeCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        if (params.length == 3 &&  (params[0] == null || params[0] instanceof MouseEvent ) && params[1] instanceof Character && params[2] instanceof Character) {
            handle((MouseEvent) params[0], (Character) params[1], (Character) params[2]);
        }

    }

    /**
     * Handles the edge event.
     *
     * @param event the mouse event
     * @param from  the character representing the starting node
     * @param to    the character representing the ending node
     */
    void handle(MouseEvent event, Character from, Character to);
}