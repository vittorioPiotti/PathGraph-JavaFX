/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.graphview;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Places vertices around a circle, ordered by the underlying
 * vertices {@code element.toString() value}.
 * 
 * @see SmartPlacementStrategy
 * 
 * @author brunomnsilva
 */
public class SmartCircularSortedPlacementStrategy implements SmartPlacementStrategy {

    private static final int RADIUS_PADDING = 4;

    @Override
    public <V, E> void place(double width, double height, SmartGraphPanel<V, E> boostSmartGraphPanel) {
        // Sort vertices by their label
        List<SmartGraphVertex<V>> vertices = new ArrayList<>(boostSmartGraphPanel.getSmartVertices());

        vertices.sort((v1, v2) -> {
            V e1 = v1.getUnderlyingVertex().element();
            V e2 = v2.getUnderlyingVertex().element();
            return boostSmartGraphPanel.getVertexLabelFor(e1).compareTo(boostSmartGraphPanel.getVertexLabelFor(e2));
        });

        //place first vertex at north position, others in clockwise manner
        Point2D center = new Point2D(width / 2, height / 2);
        int N = vertices.size();
        double angleIncrement = -360f / N;
        boolean first = true;
        Point2D p = null;

        for (SmartGraphVertex<V> vertex : vertices) {
            
            if (first) {
                //verify the smallest width and height.
                if(width > height)
                    p = new Point2D(center.getX(),
                            center.getY() - height / 2 + vertex.getRadius() * RADIUS_PADDING);
                else
                    p = new Point2D(center.getX(),
                            center.getY() - width / 2 + vertex.getRadius() * RADIUS_PADDING);

                first = false;
            } else {
                p = UtilitiesPoint2D.rotate(p, center, angleIncrement);
            }

            vertex.setPosition(p.getX(), p.getY());
        }
    }

}
