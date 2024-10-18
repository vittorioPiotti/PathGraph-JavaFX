/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A factory class for creating instances of shapes with a specified center coordinates and radius.
 *
 * @author brunomnsilva
 */
public class ShapeFactory {

    /**
     * Creates a new instance of a shape with the specified type, center coordinates, and radius.
     *
     * @param type   The type of shape to create. Supported types are "star", "circle", "triangle",
     *               "square", "pentagon", "hexagon", "heptagon", "octagon", "nonagon", "decagon",
     *               "hendecagon", and "dodecagon".
     * @param x      The center X coordinate of the shape.
     * @param y      The center Y coordinate of the shape.
     * @param radius The radius of the shape.
     * @return An instance of a shape with the specified parameters.
     * @throws IllegalArgumentException If the provided type is not recognized or if the center coordinates
     *                                  or radius are negative.
     */
    public static ShapeWithRadius<?> create(String type, double x, double y, double radius) {
        Args.requireNonNegative(x, "x");
        Args.requireNonNegative(y, "y");
        Args.requireNonNegative(radius, "radius");

        type = type.trim().toLowerCase();

        return switch (type) {
            case "star" -> new ShapeStar(x, y, radius);
            case "circle" -> new ShapeCircle(x, y, radius);
            case "triangle" -> new ShapeRegularPolygon(x, y, radius, 3);
            case "square" -> new ShapeRegularPolygon(x, y, radius, 4);
            case "pentagon" -> new ShapeRegularPolygon(x, y, radius, 5);
            case "hexagon" -> new ShapeRegularPolygon(x, y, radius, 6);
            case "heptagon" -> new ShapeRegularPolygon(x, y, radius, 7);
            case "octagon" -> new ShapeRegularPolygon(x, y, radius, 8);
            case "nonagon" -> new ShapeRegularPolygon(x, y, radius, 9);
            case "decagon" -> new ShapeRegularPolygon(x, y, radius, 10);
            case "hendecagon" -> new ShapeRegularPolygon(x, y, radius, 11);
            case "dodecagon" -> new ShapeRegularPolygon(x, y, radius, 12);
            default -> throw new IllegalArgumentException("Invalid shape type. See javadoc for available shapes.");
        };
    }
}
