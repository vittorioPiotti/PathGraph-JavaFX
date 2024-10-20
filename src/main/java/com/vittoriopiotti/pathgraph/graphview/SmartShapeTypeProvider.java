/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A provider interface for generating shape types.
 *
 * @param <T> the type of the elements for which shape types are generated
 *
 * @author  brunomnsilva
 */
public interface SmartShapeTypeProvider<T> {
    /**
     * Returns the shape type for the specified element.
     * <br/>
     * The returned value is expected to be non-null and a valid type, see {@link ShapeFactory}.
     *
     * @param vertexElement the element for which the shape type is generated
     * @return the shape type for the specified element
     */
    String valueFor(T vertexElement);
}
