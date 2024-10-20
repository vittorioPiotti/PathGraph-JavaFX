/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.dto;

/**
 * Represents a connection in a graph with a label and a cost.
 * This class is used to transfer connection data between different layers of an application.
 *
 * @author vittoriopiotti
 */

@SuppressWarnings("all")
public class ConnectionDTO {

    private final char label;
    private final int cost;

    /**
     * Constructs a new ConnectionDTO with the specified label and cost.
     *
     * @param label the character representing the label of the connection
     * @param cost  the integer representing the cost associated with the connection
     */
    public ConnectionDTO(char label, int cost) {
        this.label = label;
        this.cost = cost;
    }

    /**
     * Returns the label of the connection.
     *
     * @return the character label of the connection
     */
    public char getLabel() {
        return label;
    }



    /**
     * Returns the cost of the connection.
     *
     * @return the integer cost of the connection
     */
    public int getCost() {
        return cost;
    }


    /**
     * Returns a string representation of the ConnectionDTO.
     *
     * @return a string in the format "ConnectionDTO{label=X, cost=Y}"
     */
    @Override
    public String toString() {
        return "ConnectionDTO{" +
                "label=" + label +
                ", cost=" + cost +
                '}';
    }

}
