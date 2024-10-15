
/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.constants;

/**
 * This class defines various constants used throughout the application
 * to represent different states and directions of edges.
 *
 * <p>Available constants:</p>
 * <ul>
 *   <li>{@link #INTERRUPTED} - Represents an interrupted process</li>
 *   <li>{@link #SUCCESS} - Represents a successful process</li>
 *   <li>{@link #ERROR} - Represents an error during a process</li>
 * </ul>
 *
 * <p>Available edge directions:</p>
 * <ul>
 *   <li>{@link #BIDIRECTIONAL} - Represents a bidirectional edge</li>
 *   <li>{@link #DIRECTION_FIRST} - Represents an edge in the natural direction (first to second)</li>
 *   <li>{@link #DIRECTION_SECOND} - Represents an edge in the opposite direction (second to first)</li>
 * </ul>
 *
 * @author vittoripiotti
 */
 public class AppConstants {

    /**
     * Constant representing an interrupted process.
     */
    public static final int INTERRUPTED = 0;

    /**
     * Constant representing a successful process.
     */
    public static final int SUCCESS = 1;

    /**
     * Constant representing an error during process.
     */
    public static final int ERROR = 2;

    /**
     * Constant representing bidirectional edge.
     */
    public static final int BIDIRECTIONAL = 0;

    /**
     * Constant representing edge in natural direction.
     */
    public static final int DIRECTION_FIRST = 1;


    /**
     * Constant representing edge in opposite direction.
     */
    public static final int DIRECTION_SECOND = 2;


}
