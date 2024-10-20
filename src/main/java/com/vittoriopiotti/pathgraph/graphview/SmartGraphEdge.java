/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */



package com.vittoriopiotti.pathgraph.graphview;

import com.vittoriopiotti.pathgraph.graph.Edge;
import com.vittoriopiotti.pathgraph.graph.Vertex;

/**
 * A graph edge visually connects two {@link Vertex} of type <code>V</code>.
 * <br>
 * Concrete edge implementations used by {@link SmartGraphPanel} should
 * implement this interface as this type is the only one exposed to the user.
 * 
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 *
 * @see Vertex
 * @see SmartGraphPanel
 * 
 * @author brunomnsilva
 */
public interface SmartGraphEdge<E, V> extends SmartStylableNode {
    
     /**
     * Returns the underlying (stored reference) graph edge.
     * 
     * @return edge reference 
     * 
     * @see SmartGraphPanel
     */
    Edge<E, V> getUnderlyingEdge();

    /**
     * Returns the attached arrow of the edge, for styling purposes.
     * <br/>
     * The arrows are only used with directed graphs.
     *
     * @return arrow reference; null if does not exist.
     */
    @SuppressWarnings("unused")
    SmartStylableNode getStylableArrow();
    
    /**
     * Returns the label node for further styling.
     * 
     * @return the label node.
     */
    SmartStylableNode getStylableLabel();

}
