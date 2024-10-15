/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */
package com.vittoriopiotti.pathgraph.graph;

/**
 * Thrown when using an invalid vertex in calls of methods in {@link Graph}
 * and {@link Digraph} implementations.
 * 
 * @see Graph
 * @see Digraph
 *
 * @author brunomnsilva
 */
public class InvalidVertexException extends RuntimeException {

    /**
     * Constructs a new InvalidVertexException with a default error message.
     */
    public InvalidVertexException() {
        super("The vertex is invalid or does not belong to this graph.");
    }

    /**
     * Constructs a new InvalidVertexException with the specified error message.
     *
     * @param message the error message to display
     */
    public InvalidVertexException(String message) {
        super(message);
    }
    
}
