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
 * An edge connects two {@link Vertex} of type <code>V</code> and stores
 * an element of type <code>E</code>.
 * <br/>
 * The edge may be used in oriented and non-oriented graphs.
 * 
 * @param <E> Type of value stored in the edge
 * @param <V> Type of value stored in the vertices that this edge connects.
 * 
 * @see Graph
 * @see Digraph
 *
 *  @author brunomnsilva
 *  <p>Modified by vittoriopiotti</p>
 */
public interface Edge<E, V> {
    
    /**
     * Returns the element stored in the edge.
     * 
     * @return      stored element
     *
     * @author brunomnsilva
     */
    E element();
    
    /**
     * Returns and array of size 2, with references for both vertices at the ends
     * of an edge.
     * <br/>
     * In a {@link Digraph} the reference at {@code vertices()[0]} must be that
     * of the <i>outbound vertex</i> and at {@code vertices()[1]} that of the <i>inbound</i>
     * vertex.
     * 
     * @return an array of length 2, containing the vertices at both ends.
     *
     * @author brunomnsilva
     */
    Vertex<V>[] vertices();


    /**
     * Returns the cost associated with this edge.
     *
     * @return the cost of the edge
     *
     * @author vittoriopiotti
     */
    int getCost();


    /**
     * Returns the direction of the edge.
     * <br/>
     * In a directed graph, this value can be used to determine if the edge
     * points from the first vertex to the second or vice versa.
     *
     * @return the direction of the edge
     *
     * @author vittoriopiotti
     */
    int getDirection();



    /**
     * Sets the direction of the edge.
     * <br/>
     * This method allows to specify the direction the edge should have.
     * In a directed graph, this typically determines which vertex is the start
     * and which is the end.
     *
     * @param direction the direction to set for the edge
     *
     * @author vittoriopiotti
     */
    void setDirection(int direction);
}
