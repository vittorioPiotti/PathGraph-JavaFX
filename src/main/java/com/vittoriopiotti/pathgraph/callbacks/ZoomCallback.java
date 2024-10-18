package com.vittoriopiotti.pathgraph.callbacks;

/**
 * A callback interface for handling zoom events with a zoom level.
 *
 * @author vittoriopiotti
 */
public interface ZoomCallback extends GenericCallback {

    @Override
    default void handle(Object... params) {
        if (params.length == 1 && params[0] instanceof Double) {
            handle((Double) params[0]);
        }
    }

    /**
     * Handles the zoom event.
     *
     * @param zoomLevel the zoom level
     */
    void handle(Double zoomLevel);
}