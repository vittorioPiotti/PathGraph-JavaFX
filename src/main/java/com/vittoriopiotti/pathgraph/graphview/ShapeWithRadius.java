/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Shape;

/**
 * This interface represents a shape with a radius, providing methods to access and modify
 * properties related to the center coordinates and radius of the shape.
 *
 * @param <T> The type of the concrete underlying shape.
 *
 * @author brunomnsilva
 */
public interface ShapeWithRadius<T extends Shape> {

    /**
     * Returns the shape instance associated with this object.
     *
     * @return The shape instance.
     */
    Shape getShape();

    /**
     * Returns the property representing the center X coordinate of the shape.
     *
     * @return The property representing the center X coordinate.
     */
    DoubleProperty centerXProperty();

    /**
     * Returns the property representing the center Y coordinate of the shape.
     *
     * @return The property representing the center Y coordinate.
     */
    DoubleProperty centerYProperty();

    /**
     * Returns the property representing the radius of the shape.
     *
     * @return The property representing the radius of the shape.
     */
    DoubleProperty radiusProperty();

    /**
     * Returns the radius of the shape.
     *
     * @return The radius of the shape.
     */
    double getRadius();

    /**
     * Sets the radius of the shape to the specified value.
     *
     * @param radius The new radius value.
     */
    void setRadius(double radius);
}
