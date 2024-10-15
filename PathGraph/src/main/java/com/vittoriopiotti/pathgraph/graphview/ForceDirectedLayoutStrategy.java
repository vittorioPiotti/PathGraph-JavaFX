/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.graphview;

import javafx.geometry.Point2D;

import java.util.Collection;

/**
 * A representation of a force directed layout "strategy" used during automatic layout of nodes in a {@link SmartGraphPanel}.
 * <br/>
 * Implementing classes should compute attractive and repulsive forces according to some algorithm.
 * Typically, if two graph nodes are not adjacent the force should be dominated by the repulsive force.
 * <br/>
 * See: <a href="https://en.wikipedia.org/wiki/Force-directed_graph_drawing">Wikipedia - Force-directed graph drawing</a>
 *
 * @param <V> The generic type of {@link SmartGraphVertexNode}, i.e., the nodes of a {@link SmartGraphPanel}.
 *
 * @author brunomnsilva
 */
public abstract class ForceDirectedLayoutStrategy<V> {

    /**
     * This method must compute forces between all graph nodes. Typically, repelling forces exist between all nodes (similarly to particles
     * with the same polarity), but attractive forces only exist between adjacent nodes (nodes that are connected).
     * <br/>
     * The default behavior is to iterate over all distinct pairs of nodes and compute
     * their combined forces (attractive and repulsive), by calling {@link #computeForceBetween(SmartGraphVertexNode, SmartGraphVertexNode, double, double)}.
     * <br/>
     * Other strategies that rely on some link of global metrics should override this method.
     *
     * @param nodes       the current nodes of the graph
     * @param panelWidth    the graph panel's width
     * @param panelHeight   the graph panel's height
     */
    public void computeForces(Collection<SmartGraphVertexNode<V>> nodes, double panelWidth, double panelHeight) {
        for (SmartGraphVertexNode<V> v : nodes) {
            for (SmartGraphVertexNode<V> w : nodes) {
                if(v == w) continue;

                Point2D force = computeForceBetween(v, w, panelWidth, panelHeight);
                v.addForceVector(force.getX(), force.getY());
            }
        }
    }

    /**
     * Computes a force vector between two nodes. The force vector is the result of the attractive and repulsive force between the two.
     *
     * @param v           a node
     * @param w           another node
     * @param panelWidth    the graph panel's width
     * @param panelHeight   the graph panel's height
     * @return the force vector
     */
    protected abstract Point2D computeForceBetween(SmartGraphVertexNode<V> v, SmartGraphVertexNode<V> w, double panelWidth, double panelHeight);
}
