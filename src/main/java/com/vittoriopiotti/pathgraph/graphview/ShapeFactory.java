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

        switch(type) {
            case "star": return new ShapeStar(x, y, radius);
            case "circle": return new ShapeCircle(x, y, radius);
            case "triangle": return new ShapeRegularPolygon(x, y, radius, 3);
            case "square": return new ShapeRegularPolygon(x, y, radius, 4);
            case "pentagon": return new ShapeRegularPolygon(x, y, radius, 5);
            case "hexagon": return new ShapeRegularPolygon(x, y, radius, 6);
            case "heptagon": return new ShapeRegularPolygon(x, y, radius, 7);
            case "octagon": return new ShapeRegularPolygon(x, y, radius, 8);
            case "nonagon": return new ShapeRegularPolygon(x, y, radius, 9);
            case "decagon": return new ShapeRegularPolygon(x, y, radius, 10);
            case "hendecagon": return new ShapeRegularPolygon(x, y, radius, 11);
            case "dodecagon": return new ShapeRegularPolygon(x, y, radius, 12);

            default: throw new IllegalArgumentException("Invalid shape type. See javadoc for available shapes.");
        }
    }
}
