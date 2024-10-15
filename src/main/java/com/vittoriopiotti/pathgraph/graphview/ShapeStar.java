/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * This class represents a five-point star shape inscribed within a specified radius.
 *
 * @author brunomnsilva
 */
public class ShapeStar extends ShapeRegularPolygon {

    /**
     * Creates a new star shape enclosed in a circle of <code>radius</code>.
     * @param x the x-center coordinate
     * @param y the y-center coordinate
     * @param radius the radius of the enclosed circle
     */
    public ShapeStar(double x, double y, double radius) {
        super(x, y, radius, 10);
    }

    @Override
    protected void updatePolygon() {
        surrogate.getPoints().clear();

        double cx = centerX.doubleValue();
        double cy = centerY.doubleValue();

        double startAngle = Math.PI / 2;

        double radius = getRadius();
        double innerRadius = radius / 2;

        for (int i = 0; i < numberSides; i++) {
            double angle = startAngle + 2 * Math.PI * i / numberSides;

            double radiusToggle = (i % 2 == 0 ? radius : innerRadius);
            double px = cx - radiusToggle * Math.cos(angle);
            double py = cy - radiusToggle * Math.sin(angle);
            surrogate.getPoints().addAll(px, py);
        }
    }
}
