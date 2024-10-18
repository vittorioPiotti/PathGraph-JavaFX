package com.vittoriopiotti.pathgraph.callbacks;

/**
 * A functional interface for handling generic callbacks with variable parameters.
 *
 * @author vittoriopiotti
 */
@FunctionalInterface
public interface GenericCallback {

    /**
     * Handles a callback with variable parameters.
     *
     * @param params the parameters for the callback
     */
    void handle(Object... params);
}
