/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.geometry.Point2D;

/**
 * Class with utility methods for Point2D instances.
 * 
 * @author brunomnsilva
 */
public class UtilitiesPoint2D {
    
    /**
     * Rotate a point around a pivot point by a specific degrees amount
     * @param point point to rotate
     * @param pivot pivot point
     * @param angleDegrees rotation degrees
     * @return rotated point
     */
    public static Point2D rotate(final Point2D point, final Point2D pivot, final double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees); // Convert angle to radians

        double sin = Math.sin(angleRadians);
        double cos = Math.cos(angleRadians);

        // Translate the point relative to the pivot
        double translatedX = point.getX() - pivot.getX();
        double translatedY = point.getY() - pivot.getY();

        // Apply rotation using trigonometric functions
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;

        // Translate the rotated point back to the original position
        rotatedX += pivot.getX();
        rotatedY += pivot.getY();

        return new Point2D(rotatedX, rotatedY);
    }

    /**
     * Calculates the third vertex point that forms a triangle with segment AB as the base and C equidistant to A and B;
     * <code>angleDegrees</code> is the angle formed between A and C.
     *
     * @param pointA the point a
     * @param pointB the point b
     * @param angleDegrees desired angle (in degrees)
     * @return the point c
     */
    public static Point2D calculateTriangleBetween(final Point2D pointA, final Point2D pointB, final double angleDegrees) {
        // Calculate the midpoint of AB
        Point2D midpointAB = pointA.midpoint(pointB);

        // Calculate the perpendicular bisector of AB
        double slopeAB = (pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX());
        double perpendicularSlope = -1 / slopeAB;

        // Handle special cases where the perpendicular bisector is vertical or horizontal
        if (Double.isInfinite(perpendicularSlope)) {
            double yC = midpointAB.getY() + Math.tan(Math.toRadians(angleDegrees)) * midpointAB.getX();
            return new Point2D(midpointAB.getX(), yC);
        } else if (perpendicularSlope == 0) {
            return new Point2D(pointA.getX(), midpointAB.getY());
        }

        // Calculate the angle between AB and the x-axis
        double angleAB = Math.toDegrees(Math.atan2(pointB.getY() - pointA.getY(), pointB.getX() - pointA.getX()));

        // Calculate the angle between AB and AC
        double angleAC = angleAB + angleDegrees;

        // Calculate the coordinates of point C
        double distanceAC = pointA.distance(midpointAB) / Math.cos(Math.toRadians(angleDegrees));
        double xC = pointA.getX() + distanceAC * Math.cos(Math.toRadians(angleAC));
        double yC = perpendicularSlope * (xC - midpointAB.getX()) + midpointAB.getY();

        return new Point2D(xC, yC);
    }

}
