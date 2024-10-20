/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * This class represents a circle shape with a specified radius.
 *
 * @author brunomnsilva
 */
public class ShapeCircle implements ShapeWithRadius<Circle> {

    private final Circle surrogate;

    /**
     * Creates a circle shape.
     * @param x the x-center coordinate
     * @param y the y-center coordinate
     * @param radius the radius of the circle
     */
    public ShapeCircle(double x, double y, double radius) {
        Args.requireNonNegative(x, "x");
        Args.requireNonNegative(y, "y");
        Args.requireNonNegative(radius, "radius");

        this.surrogate = new Circle(x, y, radius);
    }

    @Override
    public Shape getShape() {
        return surrogate;
    }

    @Override
    public DoubleProperty centerXProperty() {
        return surrogate.centerXProperty();
    }

    @Override
    public DoubleProperty centerYProperty() {
        return surrogate.centerYProperty();
    }

    @Override
    public DoubleProperty radiusProperty() {
        return surrogate.radiusProperty();
    }

    @Override
    public double getRadius() {
        return surrogate.getRadius();
    }

    @Override
    public void setRadius(double radius) {
        Args.requireNonNegative(radius, "radius");

        // Only update if different
        if(Double.compare(this.getRadius(), radius) != 0) {
            surrogate.setRadius(radius);
        }
    }
}
