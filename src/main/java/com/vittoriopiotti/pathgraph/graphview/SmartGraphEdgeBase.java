/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * A base interface for boost smart graph edges that visually connects two vertices and supports additional features.
 *
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 *
 * @author brunomsilva
 * <p>Modified by vittoriopiotti</p>
 */
public interface SmartGraphEdgeBase<E, V> extends SmartGraphEdge<E, V>, SmartLabelledNode {


    /**
     * Attaches a {@link SmartArrow} to this edge, binding its position/rotation.
     *
     * @param arrow     arrow to attach
     *
     * @author brunomsilva
     */
    void attachArrow(SmartArrow arrow);

    /**
     * Returns the attached {@link SmartArrow}, if any.
     *
     * @return      reference of the attached arrow; null if none.
     *
     * @author brunomsilva
     */
    SmartArrow getAttachedArrow();

    /**
     * Applies a hover style to the edge.
     * This method changes the appearance of the edge when the mouse hovers over it.
     *
     * @author vittoriopiotti
     */
    void applyHoverStyle();

    /**
     * Reverts the edge to its default style.
     * This method resets any custom styles applied, returning the edge to its original appearance.
     *
     * @author vittoriopiotti
     */
    void applyDefaultStyle();

    /**
     * Sets the event handler for mouse click events.
     *
     * @param value The event handler to set.
     * @see EventHandler
     * @see MouseEvent
     *
     * @author vittoriopiotti
     */
    void setOnMouseClicked_(EventHandler<? super MouseEvent> value);

    /**
     * Sets the label text for the edge based on the cost value.
     *
     * @param cost The text to be displayed as the label, representing the cost of the edge.
     *
     * @author vittoriopiotti
     */
    void setCost(int cost);

    /**
     * Returns the cost associated with this edge.
     *
     * @return the cost of the edge
     *
     * @author vittoriopiotti
     */
    int getCost();

    /**
     * Applies a dash style to the edge.
     * This method modifies the appearance of the edge, setting a dashed pattern.
     *
     * @author vittoriopiotti
     */
    void applyDashStyle();

    /**
     * Resets the style of the edge to its default appearance.
     * This method clears any custom styling applied to the edge.
     *
     * @author vittoriopiotti
     */
    void resetStyle();

    /**
     * Returns the direction of the edge.
     * <br/>
     * In a directed graph, this value can be used to determine the orientation of the edge.
     *
     * @return the direction of the edge
     *
     * @author vittoriopiotti
     */
    int getDirection();

    /**
     * Sets the direction of the edge.
     * <br/>
     * This method allows specifying the direction of the edge in a directed graph.
     *
     * @param direction the direction to set for the edge
     *
     * @author vittoriopiotti
     */
    void setDirection(int direction);

    /**
     * Returns the inbound vertex node of the edge.
     *
     * @return the inbound vertex node
     *
     * @see SmartGraphVertexNode
     *
     * @author vittoriopiotti
     */
    SmartGraphVertexNode<V> getInbound();

    /**
     * Returns the outbound vertex node of the edge.
     *
     * @return the outbound vertex node
     *
     * @see SmartGraphVertexNode
     *
     * @author vittoriopiotti
     */
    SmartGraphVertexNode<V> getOutbound();

    /**
     * Returns the attached background rectangle of the edge.
     *
     * @return the attached background rectangle
     *
     * @see Rectangle
     *
     * @author vittoriopiotti
     */
    Rectangle getAttachedBackground();

    /**
     * Attaches a background rectangle to the edge.
     *
     * @param attachedBackground the background rectangle to attach
     *
     * @see Rectangle
     *
     * @author vittoriopiotti
     */
    void attachBackground(Rectangle attachedBackground);


}
