/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * This class represents a regular polygon shape with a specified number of sides and radius.
 *
 * @author brunomnsilva
 */
public class ShapeRegularPolygon implements ShapeWithRadius<Polygon> {

    protected final int numberSides;
    protected final DoubleProperty centerX, centerY;
    protected final DoubleProperty radius;

    protected final Polygon surrogate;

    /**
     * Creates a regular polygon shape with <code>numberSides</code>
     * @param x the x-center coordinate
     * @param y the y-center coordinate
     * @param radius the radius of the enclosed circle
     * @param numberSides the number of sides of the polygon
     */
    public ShapeRegularPolygon(double x, double y, double radius, int numberSides) {
        Args.requireNonNegative(x, "x");
        Args.requireNonNegative(y, "y");
        Args.requireNonNegative(radius, "radius");
        Args.requireGreaterThan(numberSides, "numberSides", 2);

        this.surrogate = new Polygon();

        this.numberSides = numberSides;

        this.centerX = new SimpleDoubleProperty(x);
        this.centerY = new SimpleDoubleProperty(y);

        this.centerX.addListener((observable, oldValue, newValue) -> updatePolygon());
        this.centerY.addListener((observable, oldValue, newValue) -> updatePolygon());

        this.radius = new SimpleDoubleProperty( radius );
        this.radius.addListener((observable, oldValue, newValue) -> updatePolygon());

        updatePolygon();
    }

    protected void updatePolygon() {
        surrogate.getPoints().clear();

        double cx = centerX.doubleValue();
        double cy = centerY.doubleValue();

        double startAngle = Math.PI / (numberSides % 2 == 0 ? numberSides : 2);

        double radius = getRadius();

        for (int i = 0; i < numberSides; i++) {
            double angle = startAngle + 2 * Math.PI * i / numberSides;
            double px = cx - radius * Math.cos(angle);
            double py = cy - radius * Math.sin(angle);
            surrogate.getPoints().addAll(px, py);
        }
    }

    /**
     * Returns the number of sides of the polygon.
     * @return the number of sides of the polygon
     */
    public int getNumberSides() {
        return numberSides;
    }

    @Override
    public Shape getShape() {
        return this.surrogate;
    }

    @Override
    public DoubleProperty centerXProperty() {
        return this.centerX;
    }

    @Override
    public DoubleProperty centerYProperty() {
        return this.centerY;
    }

    @Override
    public DoubleProperty radiusProperty() {
        return this.radius;
    }

    @Override
    public double getRadius() {
        return this.radius.doubleValue();
    }

    @Override
    public void setRadius(double radius) {
        Args.requireNonNegative(radius, "radius");

        this.radius.set(radius);
    }
}
