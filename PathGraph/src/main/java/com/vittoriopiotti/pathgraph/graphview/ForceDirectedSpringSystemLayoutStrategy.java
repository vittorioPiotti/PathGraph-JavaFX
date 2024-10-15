/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.geometry.Point2D;

/**
 * An implementation of a spring system layout strategy. This strategy allows to freely move the graph along
 * the panel, but if you have a bipartite graph, the sub-graphs will repel each other to the edges of the panel.
 * <br/>
 * Parameters:
 * <br/>
 * Repulsive force (> 0): Recommended [1, 50]. Default 25. The strength of the repulsive force between nodes.
 * Higher values result in greater repulsion.
 * <br/>
 * Attraction force (> 0): Recommended [1, 5]. Default 3. The strength of the attractive force between connected nodes.
 * Higher values result in stronger attraction. Careful, because larger values may not produce stable states.
 * <br/>
 * Attraction scale (> 0): Recommended [1, ?]. Default 10. The scale factor for attraction.
 * It determines the effectiveness of the attraction force based on the distance between connected nodes.
 * <br/>
 * Acceleration: Mandatory ]0, 1]. Default 0.8. The acceleration factor applied to node movements.
 * Higher values result in faster movements.
 *
 * @param <V> The generic type of {@link SmartGraphVertexNode}, i.e., the nodes of a {@link SmartGraphPanel}.
 *
 * @author brunomnsilva
 */
public class ForceDirectedSpringSystemLayoutStrategy<V> extends ForceDirectedLayoutStrategy<V> {

    private final double repulsiveForce;
    private final double attractionForce;
    private final double attractionScale;
    private final double acceleration;

    /* just a scaling factor so all parameters are, at most, two-digit numbers. */
    private static final double A_THOUSAND = 1000;

    /**
     * Constructs a new instance of ForceDirectedSpringGravityLayoutStrategy with default parameters, namely:
     * <br/>
     * repulsiveForce = 25, attractionForce = 3, attractionScale = 10 and acceleration = 0.8.
     */
    public ForceDirectedSpringSystemLayoutStrategy() {
        this.repulsiveForce = 25;
        this.attractionForce = 3;
        this.attractionScale = 10;
        this.acceleration = 0.8;
    }

    /**
     * Constructs a new instance of ForceDirectedSpringGravityLayoutStrategy with the specified parameters.
     *
     * @param repulsiveForce The strength of the repulsive force between nodes. Higher values result in greater repulsion.
     * @param attractionForce The strength of the attractive force between connected nodes. Higher values result in stronger attraction.
     * @param attractionScale The scale factor for attraction. It determines the effectiveness of the attraction force based on the distance between connected nodes.
     * @param acceleration The acceleration factor applied to node movements. Higher values result in faster movements.
     */
    public ForceDirectedSpringSystemLayoutStrategy(double repulsiveForce, double attractionForce, double attractionScale, double acceleration) {
        Args.requireGreaterThan(repulsiveForce, "repulsiveForce", 0);
        Args.requireGreaterThan(attractionForce, "attractionForce", 0);
        Args.requireGreaterThan(attractionScale, "attractionScale", 0);
        Args.requireGreaterThan(acceleration, "acceleration", 0);
        Args.requireInRange(acceleration, "acceleration", 0, 1);

        this.repulsiveForce = repulsiveForce;
        this.attractionForce = attractionForce;
        this.attractionScale = attractionScale;
        this.acceleration = acceleration;
    }

    @Override
    protected Point2D computeForceBetween(SmartGraphVertexNode<V> v, SmartGraphVertexNode<V> w, double panelWidth, double panelHeight) {
        // The panel's width and height are not used in this strategy
        // This allows to freely move the graph to a particular region in the panel;
        // On the other hand, e.g., in a bipartite graph the two sub-graphs will repel each other to the edges of the panel

        Point2D vPosition = v.getUpdatedPosition();
        Point2D wPosition = w.getUpdatedPosition();
        double distance = vPosition.distance(wPosition) - (v.getRadius() + w.getRadius());
        Point2D forceDirection = wPosition.subtract(vPosition).normalize();

        if (distance < 1) {
            distance = 1;
        }

        // attractive force
        Point2D attraction;
        if(v.isAdjacentTo(w)) {
            double attraction_factor = attractionForce * Math.log(distance / attractionScale);
            attraction = forceDirection.multiply(attraction_factor);
        } else {
            attraction = new Point2D(0,0);
        }

        // repelling force
        double repulsive_factor = repulsiveForce * A_THOUSAND / (distance * distance);
        Point2D repulsion = forceDirection.multiply(-repulsive_factor);

        // combine forces
        Point2D totalForce = new Point2D(attraction.getX() + repulsion.getX(),
                attraction.getY() + repulsion.getY());

        return totalForce.multiply(acceleration);
    }
}
