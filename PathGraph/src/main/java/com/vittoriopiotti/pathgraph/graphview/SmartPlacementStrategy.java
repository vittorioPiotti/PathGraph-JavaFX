/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * Contains the method that should be implemented when creating new vertex placement
 * strategies.
 * 
 * @author brunomnsilva
 */
public interface SmartPlacementStrategy {

    /**
     * Implementations of placement strategies must implement this interface.
     * <br/>
     * Should use the {@link SmartGraphVertex#setPosition(double, double) } method to place individual vertices.
     * 
     * @param <V>       Generic type for element stored at vertices.
     * @param <E>       Generic type for element stored at edges.
     * @param width     Width of the area in which to place the vertices.
     * @param height    Height of the area in which to place the vertices.
     * @param smartGraphPanel  Reference to the {@link SmartGraphPanel} whose internal vertices are to be placed.
     *                         The vertices to be placed can be obtained through {@link SmartGraphPanel#getSmartVertices()}
     *                  
     */
    <V,E> void place(double width, double height, SmartGraphPanel<V, E> smartGraphPanel);
}
