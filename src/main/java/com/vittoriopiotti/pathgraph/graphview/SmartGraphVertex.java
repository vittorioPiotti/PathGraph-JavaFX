/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.graphview;

import com.vittoriopiotti.pathgraph.graph.Graph;
import com.vittoriopiotti.pathgraph.graph.Vertex;

/**
 * Abstracts the internal representation and behavior of a visualized graph vertex.
 * 
 * @param <V> Type stored in the underlying vertex
 * 
 * @see SmartGraphPanel
 * 
 * @author brunomnsilva
 */
public interface SmartGraphVertex<V> extends SmartStylableNode {
    
    /**
     * Returns the underlying (stored reference) graph vertex.
     * 
     * @return vertex reference 
     * 
     * @see Graph
     */
    Vertex<V> getUnderlyingVertex();
    
    /**
     * Sets the position of this vertex in panel coordinates. 
     * <br/>
     * Apart from its usage in the {@link SmartGraphPanel}, this method
     * should only be called when implementing {@link SmartPlacementStrategy}.
     * 
     * @param x     x-coordinate for the vertex
     * @param y     y-coordinate for the vertex
     */
    void setPosition(double x, double y);
    
    /**
     * Return the center x-coordinate of this vertex in panel coordinates.
     * 
     * @return     x-coordinate of the vertex 
     */
    double getPositionCenterX();
    
    /**
     * Return the center y-coordinate of this vertex in panel coordinates.
     * 
     * @return     y-coordinate of the vertex 
     */
    double getPositionCenterY();
    
    /**
     * Returns the circle radius used to represent this vertex.
     * 
     * @return      circle radius
     */
    double getRadius();
    
    /**
     * Returns the label node for further styling.
     * 
     * @return the label node.
     */
    SmartStylableNode getStylableLabel();
}
