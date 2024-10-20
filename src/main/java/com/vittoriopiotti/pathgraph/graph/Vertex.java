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
 * A vertex contains an element of type <code>V</code> and is used both in
 * graphs and digraphs.
 * 
 * @param <V> Type of value stored in the vertex.
 * 
 * @see Graph
 * @see Digraph
 *
 * @author brunomnsilva
 */
public interface Vertex<V> {
    
    /**
     * Returns the element stored in the vertex.
     * 
     * @return      stored element
     */
    V element();
}
