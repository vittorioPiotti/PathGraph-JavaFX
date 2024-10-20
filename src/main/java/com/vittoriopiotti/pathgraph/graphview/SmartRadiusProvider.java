/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A provider interface for generating radii values.
 *
 * @param <T> the type of the elements for which radii are generated
 *
 * @author brunomnsilva
 */
public interface SmartRadiusProvider<T> {

    /**
     * Returns the radius for the specified element.
     * <br/>
     * The returned value is expected to be positive.
     *
     * @param vertexElement the element for which the radius is generated
     * @return the radius for the specified element
     */
    double valueFor(T vertexElement);
}
