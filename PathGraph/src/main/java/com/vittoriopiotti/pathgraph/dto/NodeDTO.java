/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.dto;

/**
 * Represents a node in the graph with a unique label.
 *
 * @param label The label of the node.
 *
 * @author vittoriopiotti
 */
public record NodeDTO(char label) {

    /**
     * Constructs a NodeDTO with the specified label.
     *
     * @param label the label of the node.
     */
    public NodeDTO {
    }

    /**
     * Returns the label of the node.
     *
     * @return the label of the node.
     */
    @Override
    public char label() {
        return label;
    }
}
