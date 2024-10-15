/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.dto;

/**
 * The ConnectionDTO class represents a data transfer object for a connection
 * in a graph. It encapsulates the details of a connection, including its
 * label and cost. This class is typically used to transfer connection data
 * between different layers of an application.
 *
 * @param label The character representing the label of the connection.
 * @param cost  The integer representing the cost associated with the connection.
 *
 * @author vittoripiotti
 */
public record ConnectionDTO(char label, int cost) {

    /**
     * Constructs a new ConnectionDTO with the specified label and cost.
     *
     * @param label the character representing the label of the connection
     * @param cost  the integer representing the cost associated with the connection
     */
    public ConnectionDTO {
    }

    /**
     * Returns the label of the connection.
     *
     * @return the character label of the connection
     */
    @Override
    public char label() {
        return label;
    }

    /**
     * Returns the cost of the connection.
     *
     * @return the integer cost of the connection
     */
    @Override
    public int cost() {
        return cost;
    }


}