/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.dto;

/**
 * Represents a node in the graph with a unique label.
 * This class is used to transfer node data between different layers of an application.
 *
 * @author vittoriopiotti
 */
@SuppressWarnings("all")
public class NodeDTO {

    private final char label;

    /**
     * Constructs a NodeDTO with the specified label.
     *
     * @param label the label of the node
     */
    public NodeDTO(char label) {
        this.label = label;
    }

    /**
     * Returns the label of the node.
     *
     * @return the label of the node
     */
    public char getLabel() {
        return label;
    }

    /**
     * Returns a string representation of the NodeDTO.
     *
     * @return a string representing the node label
     */
    @Override
    public String toString() {
        return "NodeDTO{" +
                "label=" + label +
                '}';
    }
}

