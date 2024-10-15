/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.dto;

import com.vittoriopiotti.pathgraph.constants.AppConstants;

/**
 * The EdgeDTO class represents a data transfer object for an edge in a graph.
 * It encapsulates the details of the edge, including its endpoints, cost,
 * and whether it is directed (arrowed). This class is typically used to
 * transfer edge data between different layers of an application.
 *
 * @author vittoriopiotti
 */
public class EdgeDTO {

    /**
     * The character representing the starting point of the edge.
     */
    private final char from;

    /**
     * The character representing the endpoint of the edge.
     */
    private final char to;

    /**
     * The integer representing the cost associated with the edge.
     */
    private final int cost;

    /**
     * A boolean indicating whether the edge is directed (arrowed).
     */
    private final boolean isArrowed;

    /**
     * Constructs a new EdgeDTO with the specified start point, end point,
     * cost, and direction indicator.
     *
     * @param from     the character representing the starting point of the edge
     * @param to       the character representing the endpoint of the edge
     * @param cost     the integer representing the cost associated with the edge
     * @param isArrowed a boolean indicating whether the edge is directed
     */
    public EdgeDTO(char from, char to, int cost, boolean isArrowed) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.isArrowed = isArrowed;
    }

    /**
     * Constructs a new EdgeDTO with the specified start point, end point,
     * cost, and direction. The direction determines whether the edge is
     * considered directed or not.
     *
     * @param from     the character representing the starting point of the edge
     * @param to       the character representing the endpoint of the edge
     * @param cost     the integer representing the cost associated with the edge
     * @param dir      an integer indicating the direction of the edge
     *                 (see AppConstants for direction constants)
     */
    public EdgeDTO(char from, char to, int cost, int dir) {
        if (dir == AppConstants.DIRECTION_SECOND) {
            this.from = to;
            this.to = from;
        } else {
            this.from = from;
            this.to = to;
        }
        this.cost = cost;
        this.isArrowed = dir != AppConstants.BIDIRECTIONAL;
    }

    /**
     * Returns the starting point of the edge.
     *
     * @return the character representing the starting point of the edge
     */
    public char getFrom() {
        return from;
    }

    /**
     * Returns the endpoint of the edge.
     *
     * @return the character representing the endpoint of the edge
     */
    public char getTo() {
        return to;
    }

    /**
     * Returns the cost of the edge.
     *
     * @return the integer cost of the edge
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns whether the edge is directed (arrowed).
     *
     * @return true if the edge is directed; false otherwise
     */
    public boolean getIsArrowed() {
        return isArrowed;
    }
}
