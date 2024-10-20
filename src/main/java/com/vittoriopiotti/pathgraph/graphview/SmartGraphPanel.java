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

import com.vittoriopiotti.pathgraph.app.Constants;
import com.vittoriopiotti.pathgraph.callbacks.*;
import com.vittoriopiotti.pathgraph.dto.ConnectionDTO;
import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import com.vittoriopiotti.pathgraph.dto.EdgeDTO;
import com.vittoriopiotti.pathgraph.dto.GraphDTO;
import com.vittoriopiotti.pathgraph.dto.NodeDTO;
import com.vittoriopiotti.pathgraph.graph.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import com.vittoriopiotti.pathgraph.utilities.UtilitiesCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.BiConsumer;

/**
 * JavaFX {@link Pane} that is capable of plotting a {@link Graph} or {@link Digraph}.
 * <br>
 * Be sure to call {@link #init() } after the Stage is displayed.
 * <br>
 * Whenever changes to the underlying graph are made, you should call
 * {@link #update()} to force the rendering of any new elements and, also, the
 * removal of others, if applicable.
 * <br>
 * Vertices can be dragged by the user, if configured to do so. Consequently, 
 * any connected edges will also adjust automatically to the new vertex positioning.
 *
 * @param <V> Type of element stored at a vertex
 * @param <E> Type of element stored at an edge
 *
 * @author brunomnsilva
 * <p>Modified by vittoriopiotti</p>
 */
public class SmartGraphPanel<V, E> extends Pane {




    /**
     * Configuration properties for the graph, including layout and behavior settings.
     */
    private final SmartGraphProperties graphProperties;

    /**
     * The default CSS file used for styling the graph.
     */
    protected static final String DEFAULT_CSS_FILE = "smartgraph.css";

    /**
     * The underlying graph data structure containing the vertices and edges.
     */
    private final Graph<V, E> theGraph;

    /**
     * Strategy used for placing vertices in the graph.
     */
    private final SmartPlacementStrategy placementStrategy;

    /**
     * A map that associates each vertex with its visual representation (`SmartGraphVertexNode`).
     */
    private final Map<Vertex<V>, SmartGraphVertexNode<V>> vertexNodes =new HashMap<>();

    /**
     * A map that associates each edge with its visual representation (`SmartGraphEdgeBase`).
     */
    private final Map<Edge<E, V>, SmartGraphEdgeBase<E, V>> edgeNodes =new HashMap<>();

    /**
     * A map that associates each edge with its connected vertices.
     */
    private final Map<Edge<E, V>, Tuple<Vertex<V>>> connections =new HashMap<>();

    /**
     * A map to keep track of edges already placed between two vertices.
     * It prevents placing multiple edges in the same location.
     */
    private final Map<Tuple<SmartGraphVertexNode<V>>, Integer> placedEdges = new HashMap<>();

    /**
     * Flag to indicate whether the graph has been initialized.
     */
    private boolean initialized = false;

    /**
     * Flag to determine if edges should be displayed with arrows.
     */
    private final boolean edgesWithArrows;

    /**
     * Optional label provider for vertices. If provided, it overrides any annotations or default labels.
     */
    private SmartLabelProvider<V> vertexLabelProvider;

    /**
     * Optional label provider for edges. If provided, it overrides any annotations or default labels.
     */
    private SmartLabelProvider<E> edgeLabelProvider;

    /**
     * Optional provider for custom radii of vertices.
     */
    private SmartRadiusProvider<V> vertexRadiusProvider;

    /**
     * Optional provider for custom shape types of vertices.
     */
    private SmartShapeTypeProvider<V> vertexShapeTypeProvider;

    /**
     * Property to toggle the automatic layout feature, enabling or disabling dynamic positioning of nodes.
     */
    public final BooleanProperty automaticLayoutProperty;

    /**
     * Animation timer responsible for driving the automatic layout of nodes.
     */
    private final AnimationTimer timer;

    /**
     * Strategy for automatically positioning the nodes in a force-directed layout.
     */
    private ForceDirectedLayoutStrategy<V> automaticLayoutStrategy;

    /**
     * Number of iterations to perform for automatic layout adjustments.
     * This value was obtained through experimentation.
     */
    private static final int AUTOMATIC_LAYOUT_ITERATIONS = 20;

    /**
     * Horizontal padding for the labels associated with nodes.
     */
    private final double labelPaddingHorizontal;

    /**
     * Vertical padding for the labels associated with nodes.
     */
    private final double labelPaddingVertical;

    /**
     * Background color of the labels associated with nodes.
     */
    private final Color labelBackground;

    /**
     * Corner radius for the background of the labels.
     */
    private final double labelCornerRadius;

    /**
     * Opacity of the label background.
     */
    private final double labelOpacity;

    /**
     * Style applied to the arrows of the edges.
     */
    private final int arrowStyle;






    /**
     * Callback to hide the context menu when an action is performed.
     */
    private ContextMenuCallback closeContextMenu;

    /**
     * Callback to adjust the position of nodes when they are dragged or moved.
     */
    private AdjustPositionCallback adjustPosition;




    /**
     * High-level callback invoked when an arrow (edge) is clicked, accepting the event and the associated
     * start and end vertices represented as characters. This callback is intended for higher-level logic where
     * simplified representations of the vertices (characters) involved in the edge are needed.
     */
    private EdgeCallback onClickEdge;



    /**
     * High-level callback invoked when a node (vertex) is clicked, accepting the event and the associated vertex
     * represented as a character. This is intended for higher-level logic where only a simplified representation
     * of the vertex (the character) is needed.
     */
    private NodeCallback onClickNode;




    /**
     * Callback invoked when the background is clicked, accepting the event.
     */
    private BackgroundCallback onClickBackground;








    /**
     * Low-level internal callback invoked when an arrow (edge) is clicked, accepting the event and the full edge
     * object. This method retrieves the start and end vertices of the edge, extracts their character representations
     * from their attached labels, and then calls the high-level {@code onClickEdge} with these characters.
     * The direction of the edge is taken into account to determine which vertex is considered the start and which is
     * the end, allowing for bidirectional or unidirectional edges.
     */
    private final BiConsumer<MouseEvent, Edge<E, V>> _onClickEdge = (mouseEvent, edge) -> {
        SmartGraphEdgeBase<E, V> eb = edgeNodes.get(edge);
        char v1 = eb.getInbound().getAttachedLabel().getText().charAt(0);
        char v2 = eb.getOutbound().getAttachedLabel().getText().charAt(0);
        int dir = eb.getDirection();
        char end = dir == Constants.BIDIRECTIONAL ? v2 : v1;
        char start = dir == Constants.BIDIRECTIONAL ? v1 : v2;
        if(isDoubleEdge(edge)){
            end = dir == Constants.OPPOSITE_DIRECTION ? v1 : v2;
            start = dir == Constants.OPPOSITE_DIRECTION  ? v2 : v1;
        }
        onClickEdge.handle(mouseEvent, start, end);
    };



    /**
     * Low-level internal callback invoked when a node (vertex) is clicked, accepting the event and the full vertex
     * object. This method extracts the character at a specific position from the vertex’s string representation
     * and then calls the high-level {@code onClickNode} with this character. It simplifies the vertex object to a
     * character before delegating the event to the higher-level callback.
     */
    private final BiConsumer<MouseEvent, Vertex<V>> _onClickNode = (mouseEvent, vertex) -> {
        char character = vertexNodes.get(vertex).getAttachedLabel().getText().charAt(0);
        onClickNode.handle(mouseEvent, character);
    };



    /**
     * Flag to indicate whether a drag operation is in progress.
     */
    private boolean isDragging = false;

    /**
     * Starting X-coordinate when the drag operation begins.
     */
    private double startX;

    /**
     * Starting Y-coordinate when the drag operation begins.
     */
    private double startY;



    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom parameters.
     * <br/>
     * This is the only FXML-friendly constructor (there can only be one). If you need to instantiate the default
     * parameters (besides <code>graph</code>), they are the following:
     * <ul>
     *     <li>properties - <code>new SmartGraphProperties()</code></li>
     *     <li>placementStrategy - <code>new SmartCircularSortedPlacementStrategy()</code></li>
     *     <li>cssFileURI - <code>new File("smartgraph.css").toURI()</code></li>
     *     <li>automaticLayoutStrategy - <code>new ForceDirectedSpringGravityLayoutStrategy()</code></li>
     * </ul>
     *
     * @param theGraph underlying graph
     * @param properties custom properties for the graph (e.g., node radius, edge length)
     * @param placementStrategy placement strategy for positioning the nodes
     * @param cssFile alternative CSS file to style the graph, instead of the default 'smartgraph.css'
     * @param layoutStrategy the automatic layout strategy to use for positioning nodes (e.g., force-directed layout)
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the required arguments is {@code null}
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(@NamedArg("graph") Graph<V, E> theGraph,
                           @NamedArg("properties") SmartGraphProperties properties,
                           @NamedArg("placementStrategy") SmartPlacementStrategy placementStrategy,
                           @NamedArg("cssFileURI") URI cssFile,
                           @NamedArg("automaticLayoutStrategy") ForceDirectedLayoutStrategy<V> layoutStrategy,
                           int arrowStyle,
                           double labelPaddingHorizontal,
                           double labelPaddingVertical,
                           Color labelBackground,
                           double labelCornerRadius,
                           double labelOpacity,
                           ContextMenuCallback closeContextMenu,
                           EdgeCallback onClickEdge,
                           NodeCallback onClickNode,
                           BackgroundCallback onClickBackground,
                           AdjustPositionCallback adjustPosition

    ) {

        Args.requireNotNull(theGraph, "theGraph");
        Args.requireNotNull(properties, "properties");
        Args.requireNotNull(placementStrategy, "placementStrategy");
        Args.requireNotNull(cssFile, "cssFile");
        Args.requireNotNull(layoutStrategy, "layoutStrategy");
        this.closeContextMenu = closeContextMenu;
        this.onClickEdge = onClickEdge;
        this.onClickNode = onClickNode;
        this.onClickBackground = onClickBackground;
        this.adjustPosition = adjustPosition;
        this.arrowStyle = arrowStyle;
        this.labelPaddingVertical = labelPaddingVertical;
        this.labelPaddingHorizontal = labelPaddingHorizontal;
        this.labelBackground = labelBackground;
        this.labelCornerRadius = labelCornerRadius;
        this.labelOpacity = labelOpacity;
        this.theGraph = theGraph;
        this.graphProperties = properties;
        this.placementStrategy = placementStrategy;
        this.edgesWithArrows = this.graphProperties.getUseEdgeArrow();
        this.automaticLayoutStrategy = layoutStrategy;

        loadAndApplyStylesheet(cssFile);
        initNodes();
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                runAutomaticLayout();
            }
        };
        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseDragged(this::onMouseDragged);
        this.setOnMouseClicked(this::onMouseClicked);
        this.automaticLayoutProperty = new SimpleBooleanProperty(false);
        this.automaticLayoutProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timer.start();
            } else {
                timer.stop();
            }
        });
        setNodesListeners();
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties, default circular
     * placement of vertices, default automatic spring gravity layout strategy
     * and styling from smartgraph.css.
     * @see Graph
     * @see SmartGraphProperties
     * @see SmartCircularSortedPlacementStrategy
     * @see ForceDirectedSpringGravityLayoutStrategy
     *
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if <code>theGraph</code> is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(

            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {
        this(
                new DigraphEdgeList<>(),
                new SmartGraphProperties(),
                new SmartCircularSortedPlacementStrategy(),
                new File(DEFAULT_CSS_FILE).toURI(),
                new ForceDirectedSpringGravityLayoutStrategy<>(),
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition

        );
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties, default circular
     * placement of vertices and styling from smartgraph.css.
     *
     * @param layoutStrategy the automatic layout strategy to use
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(
            ForceDirectedLayoutStrategy<V> layoutStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {
        this(
                new DigraphEdgeList<>(),
                new SmartGraphProperties(),
                new SmartCircularSortedPlacementStrategy(),
                new File(DEFAULT_CSS_FILE).toURI(),
                layoutStrategy,
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties, default automatic spring gravity layout strategy
     * and styling from smartgraph.css.
     *
     * @param properties custom properties for the graph visualization
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(
            SmartGraphProperties properties,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {
        this(
                new DigraphEdgeList<>(),
                properties,
                new SmartCircularSortedPlacementStrategy(),
                new File(DEFAULT_CSS_FILE).toURI(),
                new ForceDirectedSpringGravityLayoutStrategy<>(),
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties and styling from smartgraph.css.
     *
     * @param placementStrategy the strategy used for placing vertices in the graph
     * @param layoutStrategy the automatic layout strategy to arrange graph elements
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(
            SmartPlacementStrategy placementStrategy,
            ForceDirectedLayoutStrategy<V> layoutStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition

    ) {
        this(
                new DigraphEdgeList<>(),
                new SmartGraphProperties(),
                placementStrategy,
                new File(DEFAULT_CSS_FILE).toURI(),
                layoutStrategy,
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );

    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom placement of
     * vertices, default properties, default automatic spring gravity layout strategy
     * and styling from smartgraph.css.
     *
     * @param placementStrategy the strategy used for placing vertices in the graph;
     *                          can be <code>null</code> for default placement
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW},
     *                   it will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(
            SmartPlacementStrategy placementStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {
        this(
                new DigraphEdgeList<>(),
                new SmartGraphProperties(),
                placementStrategy,
                new File(DEFAULT_CSS_FILE).toURI(),
                new ForceDirectedSpringGravityLayoutStrategy<>(),
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and custom placement of
     * vertices, default automatic spring gravity layout strategy
     * and styling from smartgraph.css.
     *
     * @param properties custom properties for the graph visualization; can be <code>null</code> for default properties
     * @param placementStrategy the strategy used for placing vertices in the graph;
     *                          can be <code>null</code> for default placement
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW},
     *                   it will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(

            SmartGraphProperties properties,
            SmartPlacementStrategy placementStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {

        this(
                new DigraphEdgeList<>(),
                properties,
                placementStrategy,
                new File(DEFAULT_CSS_FILE).toURI(),
                new ForceDirectedSpringGravityLayoutStrategy<>(),
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );

    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties, custom placement of
     * vertices and default automatic spring gravity layout strategy.
     *
     * @param theGraph the underlying graph to visualize
     * @param properties custom properties for the graph visualization; can be <code>null</code> for default properties
     * @param placementStrategy the strategy used for placing vertices in the graph;
     *                          can be <code>null</code> for default placement
     * @param cssFile an alternative CSS file, instead of the default 'smartgraph.css';
     *                 must be a valid URI
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW},
     *                   it will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param closeContextMenu a callback to hide the context menu when triggered
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked, receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked, receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @throws IllegalArgumentException if any of the arguments is <code>null</code>
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartGraphPanel(
            Graph<V, E> theGraph,
            SmartGraphProperties properties,
            SmartPlacementStrategy placementStrategy,
            URI cssFile,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickEdge,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            AdjustPositionCallback adjustPosition
    ) {

        this(
                theGraph,
                properties,
                placementStrategy,
                cssFile,
                new ForceDirectedSpringGravityLayoutStrategy<>(),
                arrowStyle,
                labelPaddingHorizontal,
                labelPaddingVertical,
                labelBackground,
                labelCornerRadius,
                labelOpacity,
                closeContextMenu,
                onClickEdge,
                onClickNode,
                onClickBackground,
                adjustPosition
        );

    }



    /**
     * Sets all the callback functions for handling mouse events on graph elements.
     *
     * @param onClickEdge a callback invoked when an edge (arrow) is clicked,
     *                     receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked,
     *                    receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked,
     *                          receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @author vittoriopiotti
     */
    public void setAllCallbacks(ContextMenuCallback closeContextMenu,
                                EdgeCallback onClickEdge,
                                NodeCallback onClickNode,
                                BackgroundCallback onClickBackground,
                                AdjustPositionCallback adjustPosition
    ){
        this.closeContextMenu = closeContextMenu;
        this.onClickEdge = onClickEdge;
        this.onClickNode = onClickNode;
        this.onClickBackground = onClickBackground;
        this.adjustPosition = adjustPosition;
    }

    /**
     * Sets a callback to be executed to hide the context menu.
     *
     * @param closeContextMenu a {@link ContextMenuCallback} that defines the logic for hiding the context menu.
     *
     * @author vittoriopiotti
     */
    public void setContextMenuCallback(ContextMenuCallback closeContextMenu) {
        this.closeContextMenu = closeContextMenu;
    }


    /**
     * Sets a callback to be executed when an edge in the graph is clicked.
     *
     * @param onClickEdge a {@link EdgeCallback} that accepts a MouseEvent and two Characters (the source and target nodes of the clicked edge).
     *
     * @author vittoriopiotti
     */
    public void setEdgeCallback(EdgeCallback onClickEdge) {
        this.onClickEdge = onClickEdge;
    }

    /**
     * Sets a callback to be executed when a node in the graph is clicked.
     *
     * @param onClickNode a {@link NodeCallback} that accepts a MouseEvent and a Character (the clicked node).
     *
     * @author vittoriopiotti
     */
    public void setNodeCallback(NodeCallback onClickNode) {
        this.onClickNode = onClickNode;
    }

    /**
     * Sets a callback to be executed when the background of the graph is clicked.
     *
     * @param onClickBackground a {@link BackgroundCallback} that accepts a MouseEvent (the event triggered when the background is clicked).
     *
     * @author vittoriopiotti
     */
    public void setBackgroundCallback(BackgroundCallback onClickBackground) {
        this.onClickBackground = onClickBackground;
    }

    /**
     * Sets a callback to adjust the position of the graph elements.
     *
     * @param adjustPosition a {@link AdjustPositionCallback} that defines the adjustment logic for the graph's position.
     *
     * @author vittoriopiotti
     */
    public void setAdjustPositionCallback(AdjustPositionCallback adjustPosition) {
        this.adjustPosition = adjustPosition;
    }



    /**
     * Retrieves the labels of all nodes in the graph.
     *
     * @return a list of strings representing the labels of all nodes (vertices) in the graph
     *
     * @author vittoriopiotti
     */
    public List<Character> getNodesCharacters() {
        List<Character> vertices = new ArrayList<>();
        for (Map.Entry<Vertex<V>, SmartGraphVertexNode<V>> entry : vertexNodes.entrySet()) {
            vertices.add(entry.getValue().getAttachedLabel().getText().charAt(0));
        }
        return vertices;
    }

    /**
     * Generates a new node label that is not already used by existing nodes.
     * <br>
     * The label will be the first available letter from A to Z that is not currently assigned to any node.
     * If all letters are used, a null character is returned.
     *
     * @return the first available node label as a character, or a null character if all letters are taken.
     *
     * @author vittoriopiotti
     */
    public char getNewRandomNodeLabel() {
        List<Character> nodesLabel = getNodesCharacters();
        char newNodeLabel = '\0';
        if (nodesLabel.size() < Constants.MAX_NODES) {
            for (char letter = 'A'; letter <= 'Z'; letter++) {
                if (!nodesLabel.contains(letter)) {
                    newNodeLabel = letter;
                    break;
                }
            }
        }
        return newNodeLabel;
    }


    /**
     * Returns a random character label from existing node labels.
     * <br>
     * If no labels exist, returns a null character.
     *
     * @return a random character from existing node labels or a null character if none exist.
     *
     * @author vittoriopiotti
     */
    public char getExistRandomNodeLabel() {
        List<Character> nodesLabel = getNodesCharacters();
        if (!nodesLabel.isEmpty()) {
            Random random = new Random();

            int randomIndex = random.nextInt(nodesLabel.size());

            return nodesLabel.get(randomIndex);
        }
        return '\0';
    }

    /**
     * Returns the number of edges in the graph.
     *
     * @return the total number of edges.
     *
     * @author vittoriopiotti
     */
    public int getNumEdges(){
        return edgeNodes.size();
    }

    /**
     * Retrieves the number of edges connected to the node identified by the given label.
     *
     * @param label the label of the node whose connected edges are to be counted
     * @return the number of edges connected to the specified node
     */
    public int getNumConnectedEdges(char label) {
        return getConnectedEdges(label).size();
    }

    /**
     * Retrieves the number of edges connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected edges are to be counted
     * @return the number of edges connected to the specified node
     */
    public int getNumConnectedEdges(NodeDTO node) {
        return getConnectedEdges(node).size();
    }


    /**
     * Returns the number of nodes in the graph.
     *
     * @return the total number of nodes.
     *
     * @author vittoriopiotti
     */
    public int getNumNodes(){
        return vertexNodes.size();
    }

    /**
     * Checks if two edges defined by their start and end nodes are equal.
     * <br>
     * An edge is considered equal if it connects the same start and end nodes.
     *
     * @param start1 the label of the starting node of the first edge.
     * @param end1   the label of the ending node of the first edge.
     * @param start2 the label of the starting node of the second edge.
     * @param end2   the label of the ending node of the second edge.
     * @return true if the edges are equal; false otherwise.
     *
     * @author vittoriopiotti
     */
    public boolean isEqualEdge(char start1, char end1, char start2, char end2){
        if(isDoubleEdge(start1,end1)){
           return true;
        }else{
            Edge<E,V> e = getSpecificEdge(start1, end1);
            if(e != null){
                return isEqualEdge(edgeNodes.get(e),start2,end2);
            }
            return false;
        }
    }


    /**
     * Compares two edges to determine if they are equal.
     *
     * @param edge1 the first {@link EdgeDTO} to compare
     * @param edge2 the second {@link EdgeDTO} to compare
     * @return true if the two edges are equal, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isEqualEdge(EdgeDTO edge1, EdgeDTO edge2){
        return isEqualEdge(edge1.getFrom(),edge1.getTo(),edge2.getFrom(),edge2.getTo());
    }

    /**
     * Finds a connectable node from the specified starting node.
     * <br>
     * This method iterates through all the vertices and returns the first
     * node that can be connected to the specified start node. If no
     * connectable nodes exist, it returns a null character.
     *
     * @param start the label of the starting node.
     * @return the label of a connectable node, or '\0' if no connectable node is found.
     *
     * @author vittoriopiotti
     */
    public char getConnectableNode(char start){
        for(Vertex<V> v : vertexNodes.keySet()){
            char end = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(end != start) {
                Edge<E, V> e = getSpecificEdge(start, end);
                if (e == null) {
                    return end;
                }
            }
        }
        return '\0';
    }


    /**
     * Retrieves a connectable node from the graph based on the specified starting {@link NodeDTO}.
     *
     * @param node the {@link NodeDTO} representing the node
     * @return the label of the connectable node
     */
    public char getConnectableNode(NodeDTO node){
        return getConnectableNode(node.getLabel());
    }







    /**
     * Sets listeners for all vertex nodes in the graph.
     *
     * @author vittoriopiotti
     */
    private void setNodesListeners(){
        for (SmartGraphVertexNode<V> node : vertexNodes.values()) {
            node.setAllCallbacks(
                    closeContextMenu,
                    _onClickNode
            );
        }
    }







    /**
     * Renames the specified edge with a new cost label.
     *
     * @param key the edge to rename
     * @param edgeLabel the new label for the edge
     * @return true if renamed successfully, false otherwise
     * @throws IllegalArgumentException if the edge is not found
     *
     * @author vittoriopiotti
     */
    private boolean setCost(Edge<E, V> key, int edgeLabel) {
        SmartGraphEdgeBase<E, V> edgeBase = edgeNodes.get(key);
        if (edgeBase != null) {
            activeAll();
            edgeBase.setCost(edgeLabel);
            return true;
        }
        return false;
    }


    /**
     * Renames the specified edge with a new cost label.
     *
     * @param start start node of the edge to rename
     * @param end end node of the edge to rename
     * @param cost the new label for the edge
     * @return true if renamed successfully, false otherwise
     * @throws IllegalArgumentException if the edge is not found
     *
     * @author vittoriopiotti
     */
    public boolean setCost(char start, char end, int cost) {
        if(isDoubleEdge(start,end)){
            try{
                Edge<E,V> e = _getEdge(start,end);
                if(e != null){
                    return setCost(e,cost);
                }
            }catch(Exception ignored){
                return false;
            }
        }else{
            return setCost(getSpecificEdge(start,end),cost);
        }
        return false;
    }


    /**
     * Sets the cost of the edge specified by the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} representing the edge whose cost is to be set
     * @param cost the new cost to be assigned to the edge
     * @return true if the cost was successfully set, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean setCost(EdgeDTO edge, int cost) {
       return setCost(edge.getFrom(),edge.getTo(),cost);
    }


    /**
     * Renames a node and updates its connections.
     *
     * @param label the current label of the node
     * @param newLabel the new label for the node
     * @return false as the current implementation does not indicate success
     * @throws IllegalArgumentException if the node does not exist
     *
     * @author vittoriopiotti
     */
    public boolean renameNode(char label,char newLabel){
        try{
            activeAll();
            List<EdgeDTO> edges = new ArrayList<>();
            if(isExistNode(label)) {
                for (Edge<E, V> e : edgeNodes.keySet()) {
                    if (isConnectedNode(edgeNodes.get(e), getNodeBase(label))) {
                        char vi = edgeNodes.get(e).getInbound().getAttachedLabel().getText().charAt(0);
                        char vo = edgeNodes.get(e).getOutbound().getAttachedLabel().getText().charAt(0);
                        edges.add(new EdgeDTO(
                                label == vi ? newLabel : vi,
                                label == vo ? newLabel : vo,
                                edgeNodes.get(e).getCost(),
                                isDoubleEdge(e) ? edgeNodes.get(e).getDirection() : getDirectionRotated(edgeNodes.get(e).getDirection())
                        ));
                    }
                }
                deleteNode(label);
                newNode(newLabel);
                if(!edges.isEmpty()){
                    for (EdgeDTO edge : edges) {
                        newEdge(edge.getFrom(),edge.getTo(),edge.getCost(),edge.getIsArrowed());
                    }
                }
            }
        }catch(Exception ignored){

        }
        return false;
    }

    /**
     * Renames the node specified by the given {@link NodeDTO} to the new label.
     *
     * @param node     the {@link NodeDTO} representing the node to be renamed
     * @param newLabel the new label for the node
     * @return true if the node was successfully renamed, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean renameNode(NodeDTO node, char newLabel){
        return renameNode(node.getLabel(),newLabel);
    }

    /**
     * Renames the node specified by the given {@link NodeDTO} to the label of another {@link NodeDTO}.
     *
     * @param node    the {@link NodeDTO} representing the node to be renamed
     * @param newNode the {@link NodeDTO} whose label will be assigned to the original node
     * @return true if the node was successfully renamed, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean renameNode(NodeDTO node, NodeDTO newNode){
        return renameNode(node.getLabel(),newNode.getLabel());
    }




    /**
     * Creates a new node with a specified label.
     *
     * @param v the label of the new node
     * @return true if the node was created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newNode(char v){
        return newNode(0,0,v);
    }


    /**
     * Creates a new node with random label.
     *
     * @return true if the node was created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newNode(){
        return newNode(0,0,getNewRandomNodeLabel());
    }

    /**
     * Creates a new node at specified coordinates with a given label.
     *
     * @param x the x-coordinate of the new node
     * @param y the y-coordinate of the new node
     * @param v the label of the new node
     * @return true if the node was created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newNode(double x, double y,char v){
        if(v != '\0' && !isExistNode(v)) {
            activeAll();
            @SuppressWarnings("unchecked")
            Vertex<V> vertex = getModel().insertVertex((V) String.valueOf(v));
            SmartGraphVertexNode<V> vertexNode = new SmartGraphVertexNode<>(vertex, x, y, 0, "circle", true, _onClickNode, closeContextMenu);
            addVertex(vertexNode);
            updateViewModel();
            return true;
        }
        return false;
    }



    /**
     * Deletes a specified vertex from the graph.
     *
     * @param vertexToRemove the vertex to be removed
     * @return true if the vertex was deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean deleteNode(Vertex<V> vertexToRemove) {
        if(isExistNode(vertexToRemove)) {
            activeAll();
            removeNode(vertexToRemove);
            getModel().removeVertex(vertexToRemove);
            updateViewModel();
            return true;
        }
        return false;
    }


    /**
     * Deletes a node with a specified label.
     *
     * @param v the label of the node to be deleted
     * @return true if the node was deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteNode(char v){
        if(v != '\0' && isExistNode(v)) {
            for(Vertex<V> vv : vertexNodes.keySet()){
                if(vertexNodes.get(vv).getAttachedLabel().getText().charAt(0) == v){
                    return deleteNode(vv);
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Deletes the specified node from the graph based on the given node DTO.
     *
     * @param node the NodeDTO representing the node to be deleted
     * @return {@code true} if the node was successfully deleted; {@code false} otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteNode(NodeDTO node){
        return deleteNode(node.getLabel());
    }


    /**
     * Splits a specified edge into two edges.
     *
     * @param edge the edge to be split
     * @return true if the edge was split, false otherwise
     *
     *  @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    public boolean splitEdge(Edge<E, V> edge){
        SmartGraphEdgeBase<E,V> edgeBase = edgeNodes.get(edge);
        if(!isDoubleEdge(edge)){

            try{
                activeAll();
                int cost = edgeBase.getCost();
                Vertex<V> inbound = edgeBase.getInbound().getUnderlyingVertex();
                Vertex<V> outbound = edgeBase.getOutbound().getUnderlyingVertex();
                removeEdge(edgeBase);
                getModel().removeEdge(edge);
                Edge <E,V> e1 = getModel().insertEdge( outbound, inbound,  (E)generateIdEdge(),cost, Constants.NATURAL_DIRECTION);
                getModel().insertEdge( inbound, outbound,  (E)generateIdEdge(),cost, Constants.NATURAL_DIRECTION);
                updateViewModel();
                edgeNodes.get(e1).getInbound().forceMoveToFixBugOnSplit();
                return true;
            }catch(Exception ignored){

            }

        }
        return false;
    }
    /**
     * Splits the edge between two nodes specified by their node labels.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @return true if the edge was split, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean splitEdge(char v1,char v2){
        try{
            Edge <E,V> e = getGenericEdge(v1,v2);
            if(!isDoubleEdge(e)){
                return splitEdge(e);
            }
        }catch(Exception ignored){

        }
        return false;
    }

    /**
     * Splits the edge between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge to be split
     * @param end   the {@link NodeDTO} representing the ending node of the edge to be split
     * @return true if the edge was successfully split, false otherwise
     */
    public boolean splitEdge(NodeDTO start, NodeDTO end){
      return splitEdge(start.getLabel(),end.getLabel());
    }




    /**
     * Splits an edge in the graph based on the specified edge data transfer object (DTO).
     *
     * @param edge the {@link EdgeDTO} containing the information of the edge to be split,
     *             including the starting node and ending node of the edge
     * @return true if the edge was successfully split, false otherwise
     */
    public boolean splitEdge(EdgeDTO edge) {
        return splitEdge(edge.getFrom(), edge.getTo());
    }

    /**
     * Rotates the direction of a specified edge.
     *
     * @param edgeNode the edge to be rotated
     * @param direction the new direction of the edge
     * @param isReverse true if the direction is to be reversed, false otherwise
     * @return the new edge created after rotation
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("all")
    private Edge<E,V> rotateEdge(SmartGraphEdgeBase<E, V> edgeNode, int direction, boolean isReverse){
        Edge<E,V> edge = edgeNode.getUnderlyingEdge();
        int edgeLabelText = edgeNode.getCost();
        Vertex<V> inbound = edgeNode.getInbound().getUnderlyingVertex();
        Vertex<V> outbound = edgeNode.getOutbound().getUnderlyingVertex();
        removeEdge(edgeNode);
        getModel().removeEdge(edge);
        return getModel().insertEdge(isReverse ? inbound : outbound, isReverse ? outbound : inbound, (E)generateIdEdge(),edgeLabelText,direction);
    }


    /**
     * Rotates the specified edge in the graph.
     *
     * @param edge the edge to be rotated
     * @return true if the edge was successfully rotated, false otherwise
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("all")
    private boolean  rotateEdge( Edge<E, V>edge){
        if(isExistEdge(edge)) {
            activeAll();
            SmartGraphEdgeBase<E, V> edgeNode = edgeNodes.get(edge);
            if (edgeNode instanceof SmartGraphEdgeLine<E, V>) {
                final int NEW_DIRECTION = edgeNode.getDirection() == Constants.NATURAL_DIRECTION ?
                        Constants.OPPOSITE_DIRECTION
                        : edgeNode.getDirection() == Constants.OPPOSITE_DIRECTION ?
                        Constants.BIDIRECTIONAL :
                        Constants.NATURAL_DIRECTION;
                rotateEdge(edgeNode, NEW_DIRECTION, edgeNode.getDirection() != Constants.BIDIRECTIONAL);
                updateViewModel();
            } else {
                final int NEW_NATURAL_DIRECTION = edgeNode.getDirection() == Constants.NATURAL_DIRECTION ?
                        Constants.OPPOSITE_DIRECTION :
                        Constants.NATURAL_DIRECTION;
                rotateEdge(edgeNode, NEW_NATURAL_DIRECTION, false);
                rotateEdge(getOppositeEdge(edgeNode), NEW_NATURAL_DIRECTION, false);
                updateViewModel();
            }
            return true;
        }

        return false;
    }


    /**
     * Returns the edge between the specified start and end nodes.
     *
     * @param start The start node.
     * @param end   The end node.
     * @throws NullPointerException If the edge or its related data is not found.
     * @return The {@link EdgeDTO} representing the edge, or {@code null} if not found.
     *
     * @author vittoriopiotti
     */
    public EdgeDTO getEdge(char start, char end){
        if(isDoubleEdge(start,end)){
            try{
                Edge<E,V> e = _getEdge(start,end);
                if(e != null){
                    SmartGraphEdgeBase<E,V> eb = edgeNodes.get(e);
                    return new EdgeDTO(eb.getInbound().getAttachedLabel().getText().charAt(0),eb.getOutbound().getAttachedLabel().getText().charAt(0),eb.getCost(),eb.getDirection() != Constants.BIDIRECTIONAL);
                }
            }catch(Exception ignored){
                return null;
            }
        }else{
            Edge<E,V> e = getSpecificEdge(start,end);
            SmartGraphEdgeBase<E,V> eb = edgeNodes.get(e);
            return new EdgeDTO(eb.getInbound().getAttachedLabel().getText().charAt(0),eb.getOutbound().getAttachedLabel().getText().charAt(0),eb.getCost(),eb.getDirection() != Constants.BIDIRECTIONAL);
        }
        return null;
    }





    /**
     * Rotates the edge between two nodes specified by their labels to a new direction.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param direction the new direction of the edge
     * @return true if the edge was rotated, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean rotateEdge(char v1, char v2, int direction) {
        try {
            Edge<E, V> e = getSpecificEdge(v1, v2);
            if (isDoubleEdge(e) && direction == Constants.BIDIRECTIONAL) {
                return false;
            }
            if (direction != edgeNodes.get(e).getDirection()) {
                return switch (edgeNodes.get(e).getDirection()) {
                    case Constants.NATURAL_DIRECTION -> {
                        if (direction == Constants.OPPOSITE_DIRECTION) {
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        }
                        yield true;
                    }
                    case Constants.OPPOSITE_DIRECTION -> {
                        if (direction == Constants.NATURAL_DIRECTION) {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                        }
                        yield true;
                    }
                    case Constants.BIDIRECTIONAL -> {
                        if (direction == Constants.NATURAL_DIRECTION) {
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        }
                        yield true;
                    }
                    default -> false;
                };
            }
        } catch (Exception ignored) {
        }
        return false;
    }



    /**
     * Rotates the edge connecting two nodes specified by their labels.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @return true if the edge was successfully rotated, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean rotateEdge(char v1,char v2){
        try{
            SmartGraphVertexNode<V> vb1 = getNodeBase(v1);
            SmartGraphVertexNode<V> vb2 = getNodeBase(v2);
            activeAll();
            for(Edge<E,V> e : edgeNodes.keySet()){
                if(isConnectedEdge(edgeNodes.get(e),vb1,vb2)){
                    rotateEdge(e);
                }
            }
            for (Edge<E, V> e2 : edgeNodes.keySet()) {
                if (isConnectedEdge(edgeNodes.get(e2), vb2, vb1)) {
                    edgeNodes.get(e2).getInbound().forceMoveToFixBugOnSplit();
                    break;
                }
            }
            return true;
        }catch(Exception ignored){
        }
        return false;

    }

    /**
     * Rotates the edge between the two nodes specified in the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @return true if the edge was successfully rotated, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean rotateEdge(EdgeDTO edge){
       return rotateEdge(edge.getFrom(),edge.getTo());
    }

    /**
     * Rotates the edge between the two nodes specified in the given {@link EdgeDTO} to the specified direction.
     *
     * @param edge      the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @param direction the direction in which to rotate the edge
     * @return true if the edge was successfully rotated to the specified direction, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean rotateEdge(EdgeDTO edge, int direction){
        return rotateEdge(edge.getFrom(),edge.getTo(), direction);
    }




    /**
     * Deletes the specified edge from the graph.
     *
     * @param edge the edge to be deleted
     * @return true if the edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    private boolean deleteEdge (Edge<E, V> edge){
        if(isExistEdge(edge)) {
            activeAll();
            SmartGraphEdgeBase<E, V> edgeBase = edgeNodes.get(edge);
            if (isDoubleEdge(edge)) {
                try{
                    SmartGraphEdgeBase<E, V> oppositeEdge = getOppositeEdge(edgeBase);
                    int cost = oppositeEdge.getCost();
                    Vertex<V> inbound = oppositeEdge.getInbound().getUnderlyingVertex();
                    Vertex<V> outbound = oppositeEdge.getOutbound().getUnderlyingVertex();
                    int direction = oppositeEdge.getDirection();
                    Edge<E, V> opposite = oppositeEdge.getUnderlyingEdge();
                    removeEdge(oppositeEdge);
                    getModel().removeEdge(opposite);
                    removeEdge(edgeBase);
                    getModel().removeEdge(edge);
                    getModel().insertEdge(direction == Constants.NATURAL_DIRECTION ? inbound : outbound, direction == Constants.NATURAL_DIRECTION ? outbound : inbound, (E) generateIdEdge(), cost, direction);
                    updateViewModel();
                    return true;
                }catch(Exception ignored){
                }
            } else {
                removeEdge(edgeBase);
                getModel().removeEdge(edge);
                updateViewModel();
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the edge between two nodes specified by their labels.
     *
     * @param start the label of the first node
     * @param end the label of the second node
     * @return true if the edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteEdge(char start, char end){
            try{
                if(isDoubleEdge(start,end)){
                Edge<E,V> e = _getEdge(start,end);
                if(e != null){
                    return deleteEdge(e);
                }
                }else{
                    return deleteEdge(getSpecificEdge(start,end));
                }
            }catch(Exception ignored){
                return false;
            }
        return false;
    }

    /**
     * Deletes the edge between the specified nodes in the graph.
     *
     * @param edge the {@link EdgeDTO} object containing the details of the edge to be deleted,
     *             including the labels of the starting and ending nodes
     * @return true if the edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteEdge(EdgeDTO edge){
        return deleteEdge(edge.getFrom(),edge.getTo());
    }

    /**
     * Deletes all edges associated with the node specified by its label.
     *
     * @param label the label of the node for which to delete associated edges
     * @return true if at least one edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteEdge(char label) {
        List<EdgeDTO> ledto = getConnectedEdges(label);
        int c = 0;
        if(!ledto.isEmpty()) {
            for (EdgeDTO e : ledto) {
                if(!deleteEdge(e)){
                    if(deleteEdge(e.getTo(),e.getFrom())){
                        c++;
                    }
                }else{
                    c++;
                }
            }
            return c == ledto.size();
        }
        return false;
    }





    /**
     * Checks if there is a connection (edge) between two nodes
     *
     * @param start the label of the starting node
     * @param end the label of the ending node
     * @return true if there is a connection (edge) between the nodes, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isConnectedEdge(char start, char end){
        if(isDoubleEdge(start,end)){
            return true;
        }else{
            Edge<E,V> e = getGenericEdge(start, end);
            return isEqualEdge(edgeNodes.get(e),start,end) ||  isEqualEdge(edgeNodes.get(e),end,start);
        }
    }

    /**
     * Checks if the edge specified by the given {@link EdgeDTO} is connected in the graph.
     *
     * @param edge the {@link EdgeDTO} representing the edge to check for connectivity
     * @return true if the edge is connected, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isConnectedEdge(EdgeDTO edge){
        return isConnectedEdge(edge.getFrom(),edge.getTo());
    }






    /**
     * Checks if the specified label is connected to either of the two nodes
     * defined by their labels.
     *
     * @param start the label of the first node
     * @param end the label of the second node
     * @param label the label to check for connectivity
     * @return true if the specified label is connected to either of the two nodes, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isConnectedNode(char start, char end, char label) {
        return isConnectedEdge(start, label) || isConnectedEdge(end, label);
    }

    /**
     * Checks if the specified label is connected to either of the two nodes
     * defined by the given edge.
     *
     * @param edge  the {@link EdgeDTO} object containing the labels of the two nodes
     *              forming the edge
     * @param label the label of the node to check for connectivity
     * @return true if the specified label is connected to either the start or end node
     *         of the edge, false otherwise
     */
    public boolean isConnectedNode(EdgeDTO edge, char label) {
        return isConnectedEdge(edge.getFrom(), label) || isConnectedEdge(edge.getTo(), label);
    }

    /**
     * Creates a new edge in the graph based on the specified edge data transfer object (DTO).
     *
     * @param edge the {@link EdgeDTO} containing the information of the edge to be created,
     *             including the starting node, ending node, cost, and arrow indication
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(EdgeDTO edge){
        return newEdge(edge.getFrom(),edge.getTo(),edge.getCost(),edge.getIsArrowed());
    }


    /**
     * Creates a new node in the graph based on the specified node data transfer object (DTO).
     *
     * @param node the {@link NodeDTO} containing the information of the node to be created,
     *             including the node's label
     * @return true if the node was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newNode(NodeDTO node) {
        return newNode(node.getLabel());
    }


    /**
     * Checks if the specified node is connected to any other node in the graph.
     *
     * @param label the label of the node to check for connectivity
     * @return true if the specified node is connected to any other node, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isConnectedNode(char label) {
        for(Edge <E,V> e : edgeNodes.keySet()){
            char start = edgeNodes.get(e).getInbound().getAttachedLabel().getText().charAt(0);
            char end = edgeNodes.get(e).getOutbound().getAttachedLabel().getText().charAt(0);
            if(isConnectedNode(start,end,label)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified node, based on the given node DTO, is connected to any other node.
     *
     * @param node the NodeDTO representing the node to check for connections
     * @return {@code true} if the node is connected to any other node; {@code false} otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isConnectedNode(NodeDTO node) {
        return  isConnectedNode(node.getLabel());
    }

    /**
     * Retrieves a list of edges that are connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connected edges are to be retrieved
     * @return a list of {@link EdgeDTO} representing the edges connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<EdgeDTO> getConnectedEdges(char label){
        List <EdgeDTO> le = new ArrayList<>();
        for(Edge<E,V> e : edgeNodes.keySet()){
            char start = edgeNodes.get(e).getInbound().getAttachedLabel().getText().charAt(0);
            char end = edgeNodes.get(e).getOutbound().getAttachedLabel().getText().charAt(0);
            if(isConnectedNode(start,end,label)){
                try{
                    if(isDoubleEdge(start,end)){
                        Edge<E,V> e2 = _getEdge(start,end);
                        SmartGraphEdgeBase<E,V> eb = edgeNodes.get(e2);
                        char start1 = eb.getInbound().getAttachedLabel().getText().charAt(0);
                        char end1 = eb.getOutbound().getAttachedLabel().getText().charAt(0);
                        int dir = eb.getDirection();
                        int cost = eb.getCost();
                        le.add(new EdgeDTO(start1,end1,dir,cost));

                    }else{
                        Edge<E,V> e2 = getGenericEdge(start,end);
                        SmartGraphEdgeBase<E,V> eb = edgeNodes.get(e2);
                        char start1 = eb.getInbound().getAttachedLabel().getText().charAt(0);
                        char end1 = eb.getOutbound().getAttachedLabel().getText().charAt(0);
                        int dir = eb.getDirection();
                        int cost = eb.getCost();
                        le.add(new EdgeDTO(start1,end1,dir,cost));

                    }
                }catch(Exception ignored){
                }
            }
        }
        return le;
    }

    /**
     * Retrieves a list of edges connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected edges are to be retrieved
     * @return a list of EdgeDTO objects representing the edges connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<EdgeDTO> getConnectedEdges(NodeDTO node){
        return getConnectedEdges(node.getLabel());
    }

    /**
     * Retrieves a list of nodes that are connected to any node in the graph.
     *
     * @return a list of {@link NodeDTO} representing the connected nodes
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectedNodes(){
        List <NodeDTO> ln = new ArrayList<>();
        for(Vertex<V> v: vertexNodes.keySet()){
            char label = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(isConnectedNode(label)){
                ln.add(new NodeDTO(label));
            }
        }
        return ln;
    }

    /**
     * Retrieves a list of nodes that are connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connected nodes are to be retrieved
     * @return a list of {@link NodeDTO} representing the nodes connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectedNodes(char label){
        List <NodeDTO> ln = new ArrayList<>();
        for(Vertex<V> v: vertexNodes.keySet()){
            char label1 = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(isConnectedEdge(label1,label)){
                ln.add(new NodeDTO(label1));
            }
        }
        return ln;
    }

    /**
     * Retrieves a list of nodes connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected nodes are to be retrieved
     * @return a list of NodeDTO objects representing the nodes connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectedNodes(NodeDTO node) {
        return getConnectedNodes(node.getLabel());
    }

    /**
     * Retrieves a list of nodes that can be connected to any node in the graph.
     *
     * @return a list of {@link NodeDTO} representing the connectable nodes
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectableNodes(){
        List <NodeDTO> ln = new ArrayList<>();
        for(Vertex<V> v: vertexNodes.keySet()){
            char label = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(!isConnectedNode(label)){
                ln.add(new NodeDTO(label));
            }
        }
        return ln;
    }

    /**
     * Retrieves a list of nodes that can be connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connectable nodes are to be retrieved
     * @return a list of {@link NodeDTO} representing the nodes that can be connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectableNodes(char label){
        List <NodeDTO> ln = new ArrayList<>();
        for(Vertex<V> v: vertexNodes.keySet()){
            char label1 = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(!isConnectedEdge(label1,label)){
                ln.add(new NodeDTO(label1));
            }
        }
        return ln;
    }

    /**
     * Retrieves a list of nodes that can be connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node for which connectable nodes are to be retrieved
     * @return a list of NodeDTO objects representing the nodes that can be connected to the specified node
     *
     * @author vittoriopiotti
     */
    public List<NodeDTO> getConnectableNodes(NodeDTO node){
       return getConnectableNodes(node.getLabel());
    }






















    /* NEW EDGE */


    /**
     * Creates a new edge between two nodes specified by their labels with the given cost and direction.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param cost the cost of the edge
     * @param direction the direction of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char v1, char v2,int cost,int direction) {
        return newEdge(direction == Constants.OPPOSITE_DIRECTION ? v2 : v1, direction == Constants.OPPOSITE_DIRECTION ? v1 : v2, cost, direction != Constants.BIDIRECTIONAL) && rotateEdge(v1, v2, direction);
    }

    /**
     * Creates a new edge between two nodes specified by their labels with the given cost.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char v1, char v2,int cost) {
        return newEdge(v1,v2,cost,true);
    }

    /**
     * Creates a new edge between two nodes specified by their labels with the given cost and a flag indicating if the edge is arrowed.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param cost the cost of the edge
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char v1, char v2,int cost,boolean isArrowed) {
        try {
            if (
                    cost > 0 &&           //label number > 0
                            v1 != v2        //different nodes to no loop

            ) {
                if (!isExistNode(v1)) {
                    newNode(v1);
                }
                if (!isExistNode(v2)) {
                    newNode( v2);
                }
                Vertex<V> vv1 = null;
                Vertex<V> vv2 = null;
                for (Vertex<V> v : vertexNodes.keySet()) {
                    SmartGraphVertexNode<V> app = vertexNodes.get(v);
                    if (app.getAttachedLabel().getText().charAt(0) == v1) {
                        vv1 = app.getUnderlyingVertex();
                    }
                    if (app.getAttachedLabel().getText().charAt(0) ==v2) {
                        vv2 = app.getUnderlyingVertex();
                    }
                }
                try {
                    if (getTotalEdgesBetween(vv1, vv2) == 0) {
                        highLevelStaticEdgeCreation(vv2, vv1, cost, isArrowed);
                        return true;
                    } else if (getTotalEdgesBetween(vv1, vv2) == 1) {
                        return highLevelDynamicEdgeCreation(vv1, vv2, cost, isArrowed);
                    }
                } catch (Exception ignored) {
                }

            }
        }catch(Exception ignored){

        }
        return false;
    }





    /**
     * Creates a new random edge from the start node to a connectable node with the given cost and direction.
     *
     * @param start the label of the starting node
     * @param cost the cost of the edge
     * @param direction the direction of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start, int cost, int direction){
        char end = getConnectableNode(start);
        if(end != '\0' && end != start) {
            return newEdge(start, end, cost,direction);
        }
        return false;
    }


    /**
     * Creates a new random edge from the start node to a connectable node with the given cost and arrow indication.
     *
     * @param start the label of the starting node
     * @param cost the cost of the edge
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start, int cost, boolean isArrowed){
        char end = getConnectableNode(start);
        if(end != '\0' && end != start) {
            return newEdge(start, end, cost,isArrowed);
        }
        return false;
    }




    /**
     * Creates a new random edge from the start node to a connectable node with the given cost.
     *
     * @param start the label of the starting node
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start, int cost){
        char end = getConnectableNode(start);
        if(end != '\0' && end != start) {
            return newEdge(start, end, cost);
        }
        return false;
    }



    /**
     * Creates a new random edge between two available nodes with the given cost and direction.
     *
     * @param cost the cost of the edge
     * @param direction the direction of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(int cost, int direction){
        for(Vertex<V> v : vertexNodes.keySet()){
            char start = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if( newEdge(start,cost,direction)){
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new random edge between two available nodes with the given cost and arrow indication.
     *
     * @param cost the cost of the edge
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(int cost, boolean isArrowed){
        for(Vertex<V> v : vertexNodes.keySet()){
            char start = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(newEdge(start, cost,isArrowed)){
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new random edge between two available nodes with the given cost.
     *
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(int cost){
        for(Vertex<V> v : vertexNodes.keySet()){
            char start = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(newEdge(start, cost)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new random edge between two available nodes with the given cost.
     *
     * @param start the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start){
            return newEdge(start,(int) (Math.random() * 1000) + 1);

    }


    /**
     * Creates a new random edge between two available nodes with a specified arrow indication.
     *
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(boolean isArrowed){
        for(Vertex<V> v : vertexNodes.keySet()){
            char start = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            if(newEdge(start, isArrowed)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates a new random edge between two available nodes with a random cost.
     *
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(){
        return   newEdge((int) (Math.random() * 1000) + 1);
    }























    /**
     * Sets the arrow direction for the edge between two nodes specified by their labels.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the arrow direction was successfully set, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean setArrow(char v1,char v2,boolean isArrowed){
        try{
            Edge<E, V> e = getSpecificEdge(v1, v2);
            if(isDoubleEdge(e)  ){
                return false;
            }else{
                if(!isArrowed) {
                    return rotateEdge(v1, v2, Constants.BIDIRECTIONAL);
                }else{
                    if(edgeNodes.get(e).getDirection() == Constants.BIDIRECTIONAL){
                        return rotateEdge(v1, v2);
                    }else{
                        return false;
                    }
                }
            }
        }catch(Exception ignored){
        }
        return false;
    }



    /**
     * Sets the arrow status for the edge between the two nodes specified in the given {@link EdgeDTO}.
     *
     * @param edge      the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @param isArrowed true if the edge should be arrowed, false otherwise
     * @return true if the arrow status was successfully set, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean setArrow(EdgeDTO edge,boolean isArrowed){
        return setArrow(edge.getFrom(),edge.getTo(),isArrowed);
    }



    /**
     * Get edge direction between two nodes.
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer representing the direction of the edge between the two nodes:
     *         {@link Constants#BIDIRECTIONAL}, {@link Constants#NATURAL_DIRECTION},
     *         or {@link Constants#OPPOSITE_DIRECTION}.
     *
     * @author vittoriopiotti
     */
    public int getDirection(char v1, char v2) {
            if(isDoubleEdge(v1,v2)){
                try{
                    Edge<E,V> e = _getEdge(v1,v2);
                    if(e != null){
                        return edgeNodes.get(e).getDirection();
                    }
                }catch(Exception ignored){
                    return Constants.ERROR;
                }
            }else{
                return Objects.requireNonNull(getSpecificEdge(v1, v2)).getDirection();
            }
            return Constants.ERROR;
    }

    /**
     * Retrieves the direction of the edge specified by the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} representing the edge whose direction is to be retrieved
     * @return the direction of the edge as an integer
     *
     * @author vittoriopiotti
     */
    public int getDirection(EdgeDTO edge) {
      return getDirection(edge.getFrom(),edge.getTo());
    }

    /**
     * Retrieves the direction of the edge between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return an integer representing the direction of the edge
     */
    public int getDirection(NodeDTO start, NodeDTO end) {
        return getDirection(start.getLabel(),end.getLabel());
    }











    /**
     * Get edge cost
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer representing the cost of the edge between the two nodes
     *
     * @author vittoriopiotti
     */
    public int getCost(char v1, char v2) {
        if(isDoubleEdge(v1,v2)){
            try{
                Edge<E,V> e = _getEdge(v1,v2);
                if(e != null){
                    return edgeNodes.get(e).getCost();
                }
            }catch(Exception ignored){
                return Constants.ERROR;
            }
        }else{
            return Objects.requireNonNull(getSpecificEdge(v1, v2)).getCost();
        }
        return Constants.ERROR;
    }

    /**
     * Retrieves the cost associated with the specified edge.
     * <br>
     * This method returns the cost of the edge defined by the given {@code EdgeDTO},
     * based on the nodes it connects.
     *
     * @param edge the {@code EdgeDTO} representing the edge.
     * @return the cost of the edge.
     */
    public int getCost(EdgeDTO edge){
        return getCost(edge.getFrom(),edge.getTo());
    }


    /**
     * Retrieves the cost associated with the edge between the given nodes.
     * <br>
     * This method returns the cost of the edge connecting the specified
     * {@code NodeDTO} objects.
     *
     * @param start the starting node as a {@code NodeDTO}.
     * @param end the ending node as a {@code NodeDTO}.
     * @return the cost of the edge between the nodes.
     */
    public int getCost(NodeDTO start, NodeDTO end){
        return getCost(start.getLabel(),end.getLabel());
    }


    /**
     * Gets the rotated direction of the edge between two nodes.
     * <br>
     * This method rotates the direction of the edge like a wheel, returning:
     * <ul>
     *     <li>{@link Constants#OPPOSITE_DIRECTION} if the original direction was {@link Constants#NATURAL_DIRECTION}</li>
     *     <li>{@link Constants#BIDIRECTIONAL} if the original direction was {@link Constants#OPPOSITE_DIRECTION}</li>
     *     <li>{@link Constants#NATURAL_DIRECTION} if the original direction was {@link Constants#BIDIRECTIONAL}</li>
     * </ul>
     *
     * @param dir the original direction of the edge
     * @return an integer representing the new direction of the edge
     *
     * @author vittoriopiotti
     */

    public int getDirectionRotated(int dir){
        return dir == Constants.NATURAL_DIRECTION ? Constants.OPPOSITE_DIRECTION : dir == Constants.OPPOSITE_DIRECTION ? Constants.NATURAL_DIRECTION : Constants.BIDIRECTIONAL;
    }





    /**
     * Takes a screenshot of the current view, optionally allowing for an animated screenshot.
     *
     * @param isAnimated true to capture an animated screenshot, false for a static one
     * @return a CompletableFuture containing the result of the screenshot operation
     * @see UtilitiesCapture#takeScreenshot(Pane, boolean)
     *
     * @author vittoriopiotti
     */
    public CompletableFuture<Integer> takeScreenshot(boolean isAnimated) {
        return UtilitiesCapture.takeScreenshot(this, isAnimated);
    }


    /**
     * Takes a screenshot of the current view with animation enabled by default.
     *
     * @return a CompletableFuture containing the result of the screenshot operation
     * @see UtilitiesCapture#takeScreenshot(Pane, boolean)
     *
     * @author vittoriopiotti
     */
    public CompletableFuture<Integer> takeScreenshot() {
        return UtilitiesCapture.takeScreenshot(this, true);
    }



    /**
     * Uploads a JSON file to set the graph data.
     *
     * @param file the JSON file to upload
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the upload was successful
     *         - {@link Constants#ERROR} if an error occurred during upload
     *         - {@link Constants#INTERRUPTED} if the file is null
     *
     * @author vittoriopiotti
     */
    public int uploadJSON(File file){
        if(file != null) {
            try {
                GraphDTO dto = new GraphDTO(file);
                setGraph(dto,true);
                return Constants.SUCCESS;
            } catch (Exception ignored) {
                return Constants.ERROR;
            }
        }

        return Constants.INTERRUPTED;
    }


    /**
     * Opens a file chooser dialog to upload a JSON file and set the graph data.
     *
     * @param window the parent window for the file chooser dialog
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the upload was successful
     *         - {@link Constants#ERROR} if an error occurred during upload
     *         - {@link Constants#INTERRUPTED} if no file was selected
     *
     * @author vittoriopiotti
     */
    public int uploadJSON(Window window){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carica file JSON...");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(window);
        if(file != null) {
            try {
                GraphDTO dto = new GraphDTO(file);
                setGraph(dto,true);
                return Constants.SUCCESS;
            } catch (Exception ignored) {
                return Constants.ERROR;
            }
        }

        return Constants.INTERRUPTED;
    }



    /**
     * Downloads the current graph data to a JSON file, using a file chooser dialog.
     *
     * @param window the parent window for the file chooser dialog
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the download was successful
     *         - {@link Constants#ERROR} if an error occurred during download
     *         - {@link Constants#INTERRUPTED} if no file was selected
     *
     * @author vittoriopiotti
     */
    public int downloadJSON(Window window) {
        return downloadJSON(window,getGraph());
    }

    /**
     * Downloads the current graph data to a JSON file.
     *
     * @param file the file to which the graph data will be written
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the download was successful
     *         - {@link Constants#ERROR} if an error occurred during download
     *         - {@link Constants#INTERRUPTED} if the file is null
     *
     * @author vittoriopiotti
     */
    public int downloadJSON(File file) {
        return downloadJSON(file,getGraph());
    }

    /**
     * Downloads the given graph data to a JSON file, using a file chooser dialog.
     *
     * @param window the parent window for the file chooser dialog
     * @param dto the graph data to be downloaded
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the download was successful
     *         - {@link Constants#ERROR} if an error occurred during download
     *         - {@link Constants#INTERRUPTED} if no file was selected
     *
     * @author vittoriopiotti
     */
    public int downloadJSON(Window window,GraphDTO dto) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("graph.json");
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(dto.getGraphJson());
                return Constants.SUCCESS;
            } catch (IOException ignored) {
                return Constants.ERROR;
            }
        } else {
            return Constants.INTERRUPTED;
        }
    }

    /**
     * Downloads the given graph data to a specified JSON file.
     *
     * @param file the file to which the graph data will be written
     * @param dto the graph data to be downloaded
     * @return a status code indicating the result of the operation:
     *         - {@link Constants#SUCCESS} if the download was successful
     *         - {@link Constants#ERROR} if an error occurred during download
     *         - {@link Constants#INTERRUPTED} if the file is null
     *
     * @author vittoriopiotti
     */
    public int downloadJSON(File file,GraphDTO dto) {
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(dto.getGraphJson());
                return Constants.SUCCESS;
            } catch (IOException ignored) {
                return Constants.ERROR;
            }
        } else {
            return Constants.INTERRUPTED;
        }
    }




    /**
     * Highlights the specified path in the graph by applying styles to the vertices and edges.
     *
     * This method activates all nodes, applies a dashed style to all edges, and updates the style
     * of the nodes and edges that are part of the provided path. If any node in the path does not exist,
     * the method will return false.
     *
     * @param lpn a list of NodeDTO objects representing the path to highlight
     * @return true if the path was successfully highlighted; false if any node in the path does not exist
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("all")
    public boolean showPath(List<NodeDTO> lpn) {
        try {
            char n = lpn.get(0).getLabel();
            if (isExistNode(n)) {
                activeAllNodes();
                for (Edge<E, V> e : edgeNodes.keySet()) {
                    edgeNodes.get(e).applyDashStyle();
                }
                SmartGraphVertexNode<V> vn = getNodeBase(n);
                vn.removeStyleClass("vertex");
                vn.removeStyleClass("vertex-deactivated");
                vn.removeStyleClass("vertex-deactivated-first");
                vn.addStyleClass("vertex-hover");
                for (int i = 1; i < lpn.size(); i++) {
                    char _n = lpn.get(i).getLabel();
                    n = lpn.get(i - 1).getLabel();
                    if (isExistNode(n) && isExistNode(_n)) {
                        vn = getNodeBase(n);
                        SmartGraphVertexNode<V> _vn = getNodeBase(_n);
                        for (Edge<E, V> e : edgeNodes.keySet()) {

                                if (isDoubleEdge(e)) {
                                    if (edgeNodes.get(e).getDirection() == Constants.NATURAL_DIRECTION && isEqualEdge(edgeNodes.get(e), _vn, vn)) {
                                        edgeNodes.get(e).resetStyle();
                                    } else if (edgeNodes.get(e).getDirection() == Constants.OPPOSITE_DIRECTION && isEqualEdge(edgeNodes.get(e), _vn, vn)) {
                                        getOppositeEdge(edgeNodes.get(e)).resetStyle();
                                    }
                                } else if (e.getDirection() == Constants.BIDIRECTIONAL
                                        ? isConnectedEdge(edgeNodes.get(e), vn, _vn)
                                        : isEqualEdge(edgeNodes.get(e), vn, _vn)
                                ) {
                                    edgeNodes.get(e).resetStyle();
                                }
                        }

                        _vn.removeStyleClass("vertex");
                        _vn.removeStyleClass("vertex-deactivated");
                        _vn.removeStyleClass("vertex-deactivated-first");
                        _vn.addStyleClass("vertex-hover");
                    } else {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception ignored) {
        }
        return false;
    }



    /**
     * Clears the graph by removing all vertices and edges, with a scaling transition effect.
     * <br>
     * This method uses a CompletableFuture to handle the operation asynchronously. The graph will be
     * scaled down to zero over a duration of 0.7 seconds. After the transition, all nodes will be deleted,
     * and the graph will reset its scale to 1.0.
     *
     * @return a CompletableFuture that will be completed once the graph has been cleared
     *
     * @author vittoriopiotti
     */
    public CompletableFuture<Void> clearGraph() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            adjustPosition.handle();
            double currentScaleX = this.getScaleX();
            double currentScaleY = this.getScaleY();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.7), this);
            scaleTransition.setFromX(currentScaleX);
            scaleTransition.setFromY(currentScaleY);
            scaleTransition.setToX(0.0);
            scaleTransition.setToY(0.0);
            scaleTransition.setOnFinished(event -> {
                if (!vertexNodes.isEmpty()) {
                    do {
                        deleteNode(getLastElement());
                    } while (!vertexNodes.isEmpty());
                }
                this.setScaleX(1.0);
                this.setScaleY(1.0);
                future.complete(null);
            });

            scaleTransition.play();
        });
        return future;
    }



























    /**
     * Resets the styles of all nodes and edges in the graph.
     * <br>
     * This method activates all nodes and edges, applying their default styles.
     *
     * @author vittoriopiotti
     */
    public void resetStyles(){
        activeAll();
    }

    /**
     * Retrieves the current state of the graph as a {@link GraphDTO} object.
     * <br>
     * This method collects all vertices and edges in the graph,
     * creating a list of {@link NodeDTO} and {@link EdgeDTO} objects that represent
     * the graph's structure and properties.
     *
     * <p>
     * The returned {@link GraphDTO} object is <b>public</b>, allowing access
     * to its nodes and edges from outside this class.
     * </p>
     *
     * @return a {@link GraphDTO} object containing the lists of nodes and edges.
     *         Each node is represented by a {@link NodeDTO} containing its label,
     *         while each edge is represented by a {@link EdgeDTO} detailing its
     *         connection, cost, and direction.
     *
     * @author vittoriopiotti
     */
    public GraphDTO getGraph() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        vertexNodes.keySet().forEach(v -> {
            char label = vertexNodes.get(v).getAttachedLabel().getText().charAt(0);
            nodes.add(new NodeDTO(label));
        });
        for (Edge<E,V> e : edgeNodes.keySet()) {
            char i = edgeNodes.get(e).getInbound().getAttachedLabel().getText().charAt(0);
            char o = edgeNodes.get(e).getOutbound().getAttachedLabel().getText().charAt(0);
            int d = edgeNodes.get(e).getDirection();
            int l = edgeNodes.get(e).getCost();
            edges.add(new EdgeDTO(
                    isDoubleEdge(e) ? i : d == Constants.OPPOSITE_DIRECTION ? i : o,
                    isDoubleEdge(e) ? o : d == Constants.OPPOSITE_DIRECTION ? o : i,
                    l,
                    d
            ));
        }
        return new GraphDTO(nodes, edges);
    }


    /**
     * Retrieves the list of nodes in the graph.
     *
     * @return a list of {@link NodeDTO} representing the nodes.
     * These data transfer objects facilitate graph operations.
     */
    public List<NodeDTO> getNodes() {
        return getGraph().getNodes();
    }

    /**
     * Retrieves the list of edges in the graph.
     *
     * @return a list of {@link EdgeDTO} representing the edges.
     * These data transfer objects facilitate graph operations.
     *
     * @author vittoripiotti
     */
    public List<EdgeDTO> getEdges() {
        return getGraph().getEdges();
    }

    /**
     * Retrieves the connections between nodes in the graph.
     *
     * @return a map of {@link NodeDTO} to lists of {@link ConnectionDTO}.
     * This structure enables efficient management of graph connections.
     *
     * @author vittoripiotti
     */
    public Map<NodeDTO, List<ConnectionDTO>> getConnections() {
        return getGraph().getConnections();
    }


    /**
     * Retrieves the graph's current state as a JSON string, including nodes and edges.
     * This representation allows for easy serialization and data interchange.
     *
     * @return a JSON string of the current {@link GraphDTO} object.
     *
     * @author vittoripiotti
     */
    public String getGraphJson() {
        return getGraph().getGraphJson();
    }

    /**
     * Retrieves the current state of the nodes as a JSON string.
     *
     * @return a JSON string representing the graph's nodes.
     *
     * @author vittoripiotti
     */
    public String getNodesJson() {
        return getGraph().getNodesJson();
    }

    /**
     * Retrieves the current state of the edges as a JSON string.
     *
     * @return a JSON string representing the graph's edges.
     *
     * @author vittoripiotti
     */
    public String getEdgesJson() {
        return getGraph().getEdgesJson();
    }

    /**
     * Generates a JSON representation of the node connections.
     * <br>
     * The JSON contains nodes and their corresponding connections,
     * including the destination node and the cost of each connection.
     *
     * @return a formatted JSON string representing the connections
     */
    public String getConnectionsJson() {
        return getGraph().getConnectionsJson();
    }


    /**
     * Checks if the given edge is a double edge.
     * <br>
     * An edge is considered a double edge if it is not an instance of
     * {@link SmartGraphEdgeLine}. This helps differentiate between single and
     * double edges in the graph.
     *
     * @param edge the edge to check
     * @return true if the edge is a double edge; false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isDoubleEdge(Edge<E, V> edge) {
        return !(edgeNodes.get(edge) instanceof SmartGraphEdgeLine);
    }

    /**
     * Checks if the edge connecting the given start and end characters is a double edge.
     * <br>
     * An edge is considered a double edge if it is not an instance of
     * {@link SmartGraphEdgeLine}. This helps differentiate between single and
     * double edges in the graph.
     *
     * <p>
     * This method retrieves the specific edge using the provided start and end
     * characters, and then checks its type to determine if it is a double edge.
     * </p>
     *
     * @param start the starting character of the edge
     * @param end the ending character of the edge
     * @return true if the edge is a double edge; false otherwise
     *
     * c
     */
    public boolean isDoubleEdge(char start, char end) {
        SmartGraphEdgeBase<E,V> eb = edgeNodes.get(getSpecificEdge(start, end));
        return eb != null && !(eb instanceof SmartGraphEdgeLine);
    }

    /**
     * Checks if the specified edge is bidirectional.
     * <br>
     * This method verifies if there is a bidirectional edge between
     * the nodes in the given {@code EdgeDTO}, meaning an edge exists
     * in both directions.
     *
     * @param edge the {@code EdgeDTO} representing the edge to check.
     * @return {@code true} if the edge is bidirectional, {@code false} otherwise.
     *
     * @author vittoriopiotti
     */
    public boolean isDoubleEdge(EdgeDTO edge){
        return isDoubleEdge(edge.getFrom(),edge.getTo());
    }

    /**
     * Checks if there is a bidirectional edge between the given nodes.
     * <br>
     * This method verifies if an edge exists in both directions between
     * the nodes represented by the given {@code NodeDTO} objects.
     *
     * @param start the starting node as a {@code NodeDTO}.
     * @param end the ending node as a {@code NodeDTO}.
     * @return {@code true} if there is a bidirectional edge, {@code false} otherwise.
     *
     * @author vittoriopiotti
     */
    public boolean isDoubleEdge(NodeDTO start, NodeDTO end){
        return isDoubleEdge(start.getLabel(),end.getLabel());
    }

    /**
     * Retrieves a specific edge between two vertices identified by their labels.
     * <br>
     * This method searches for an edge connecting the vertices labeled
     * v1 and v2. It accounts for both single and double edges, returning
     * the first matching edge found.
     *
     * @param v1 the label of the first vertex
     * @param v2 the label of the second vertex
     * @return the Edge<E, V> connecting the specified vertices, or null if none exists
     *
     * @author vittoriopiotti
     */
    private Edge<E,V> getSpecificEdge(char v1,char v2){
        try {
            SmartGraphVertexNode<V> vb1 = getNodeBase(v1);
            SmartGraphVertexNode<V> vb2 = getNodeBase(v2);
            for (Edge<E, V> e : edgeNodes.keySet()) {
                if(isDoubleEdge(e)){
                    if(isEqualEdge(edgeNodes.get(e),vb2,vb1) && edgeNodes.get(e).getDirection() == Constants.NATURAL_DIRECTION ) {
                        return e;
                    }else if(isEqualEdge(edgeNodes.get(e),vb1,vb2)) {
                        return e;
                    }
                }else {
                    if(isConnectedEdge(edgeNodes.get(e), vb2, vb1)) {
                        if(edgeNodes.get(e).getDirection() == Constants.BIDIRECTIONAL){
                            return e;
                        }else{
                            if(isEqualEdge(edgeNodes.get(e),vb1,vb2)){
                                return e;
                            }
                        }
                    }
                }
            }
        }catch (Exception ignored){
        }
        return null;
    }







    /**
     * Retrieves a generic edge connecting two vertices represented by characters.
     *
     * @param v1 the first vertex character
     * @param v2 the second vertex character
     * @return the edge connecting the specified vertices, or null if no such edge exists
     *
     * @author vittoriopiotti
     */
    private Edge<E,V> getGenericEdge(char v1,char v2){
        try {
            SmartGraphVertexNode<V> vb1 = getNodeBase(v1);
            SmartGraphVertexNode<V> vb2 = getNodeBase(v2);
            for (Edge<E, V> e : edgeNodes.keySet()) {
                if(!isDoubleEdge(e) && isConnectedEdge(edgeNodes.get(e),vb2,vb1) ) {
                        return e;
                    }
            }
        }catch (Exception ignored){
        }
        return null;
    }






    /**
     * Retrieves the node base corresponding to a specified vertex character.
     *
     * @param v the vertex character to search for
     * @return the SmartGraphVertexNode associated with the specified vertex character, or null if not found
     *
     * @author vittoriopiotti
     */
    private SmartGraphVertexNode<V> getNodeBase(char v){
        for (Vertex<V> e : vertexNodes.keySet()) {
            if(vertexNodes.get(e).getAttachedLabel().getText().charAt(0) == v){
                return vertexNodes.get(e);
            }
        }
        return null;
    }


    /**
     * Creates a dynamic edge between two vertices if it does not already exist.
     *
     * @param v1 the first vertex
     * @param v2 the second vertex
     * @param cost the cost associated with the edge
     * @param isArrowed indicates if the edge should be arrowed
     * @return true if the edge was created successfully, false if it already exists
     *
     * @author vittoriopiotti
     */
    private boolean highLevelDynamicEdgeCreation(Vertex<V> v1,Vertex<V> v2,int cost,boolean isArrowed ){
        if(!isExistEdge(v1,v2)) {
            SmartGraphVertexNode<V> vb1 = vertexNodes.get(v1);
            SmartGraphVertexNode<V> vb2 = vertexNodes.get(v2);
            boolean flag = false;
            for (Edge<E, V> e : edgeNodes.keySet()) {
                if (isConnectedEdge(edgeNodes.get(e), vb2, vb1)) {
                    if (e.getDirection() == Constants.BIDIRECTIONAL) {
                        flag = true;
                        rotateEdge(e);
                        break;
                    }
                }
            }
            if (flag) {
                adjustEdgeDirection(vb1, vb2);
            }
            Edge<E, V> e = highLevelStaticEdgeCreation(v1, v2, cost,isArrowed);
            for (Edge<E, V> e2 : edgeNodes.keySet()) {
                if (isConnectedEdge(edgeNodes.get(e2), vb2, vb1)) {
                    if (!e.equals(e2)) {
                        rotateEdge(e2);
                        rotateEdge(e);
                        break;
                    }
                }
            }
            adjustEdgeDirection(vb2, vb1);
            for (Edge<E, V> e2 : edgeNodes.keySet()) {
                if (isConnectedEdge(edgeNodes.get(e2), vb2, vb1)) {
                    edgeNodes.get(e2).getInbound().forceMoveToFixBugOnSplit();
                    break;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Adjusts the direction of an edge connecting two vertex nodes.
     *
     * @param vb1 the first vertex node
     * @param vb2 the second vertex node
     *
     * @author vittoriopiotti
     */
    private void adjustEdgeDirection(SmartGraphVertexNode<V> vb1,SmartGraphVertexNode<V> vb2){
        for (Edge<E,V> e : edgeNodes.keySet()) {
            if(isEqualEdge(edgeNodes.get(e),vb1,vb2)){
                rotateEdge(e);
                break;
            }
        }
    }

    /**
     * Checks if two edges are equal based on their inbound and outbound vertex nodes.
     *
     * @param e the edge to compare
     * @param v1 the first vertex node
     * @param v2 the second vertex node
     * @return true if the edges are equal, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isEqualEdge(SmartGraphEdgeBase <E,V> e, SmartGraphVertexNode<V> v1, SmartGraphVertexNode<V> v2  ) {
        return e.getInbound() == v2 && e.getOutbound() == v1;
    }


    /**
     * Checks if two edges are equal based on their inbound and outbound vertex nodes.
     *
     * @param e the edge to compare
     * @param v1 the first vertex node
     * @param v2 the second vertex node
     * @return true if the edges are equal, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isEqualEdge(SmartGraphEdgeBase <E,V> e, char v1, char v2  ) {
        return e != null && (e.getInbound().getAttachedLabel().getText().charAt(0) == v1 && e.getOutbound().getAttachedLabel().getText().charAt(0) == v2);
    }

    /**
     * Checks if an edge is connected to the specified vertices.
     *
     * @param e the edge to check
     * @param v1 the first vertex node
     * @param v2 the second vertex node
     * @return true if the edge is connected to the specified vertices, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isConnectedEdge(SmartGraphEdgeBase <E,V> e, SmartGraphVertexNode<V> v1, SmartGraphVertexNode<V> v2  ){
        return  e != null &&  (isEqualEdge(e,v2,v1) || isEqualEdge(e,v1,v2));

    }


    /**
     * Checks if a node is connected to a specified edge.
     *
     * @param e the edge to check
     * @param v1 the vertex node to check for connection
     * @return true if the node is connected to the edge, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isConnectedNode(SmartGraphEdgeBase <E,V> e, SmartGraphVertexNode<V> v1  ){
        return  e != null &&  (e.getOutbound().equals(v1) || e.getInbound().equals(v1)) ;

    }


    /**
     * Creates a static edge between two vertices with the specified cost and direction.
     *
     * @param v1 the outbound vertex
     * @param v2 the inbound vertex
     * @param cost the cost associated with the edge
     * @param isArrowed indicates if the edge should be arrowed
     * @return the created edge
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    private Edge <E,V> highLevelStaticEdgeCreation(Vertex<V> v1,Vertex<V> v2,int cost,boolean isArrowed){
        Edge <E,V> e = this.getModel().insertEdge(
                v2,                                     //inbound node
                v1,                                     //outbound node
                (E) generateIdEdge(),                   //edge id
                cost,                                   //edge label
                isArrowed ? Constants.NATURAL_DIRECTION  :  Constants.BIDIRECTIONAL     //edge direction: NATURAL_DIRECTION, OPPOSITE_DIRECTION, BIDIRECTIONAL
        );
        updateViewModel();
        return e;
    }

    /**
     * Checks if a node exists based on the specified vertex character.
     *
     * @param v the vertex character to search for
     * @return true if the node exists, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistNode(char v){
        for(Vertex<V> vv : vertexNodes.keySet()){
            if(vertexNodes.get(vv).getAttachedLabel().getText().charAt(0) == v){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a node with the specified label exists in the graph.
     *
     * @param node the {@link NodeDTO} object containing the label of the node to check for existence
     * @return true if the node exists in the graph, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistNode(NodeDTO node){
       return isExistNode(node.getLabel());
    }

    /**
     * Checks if a specified vertex node exists in the graph.
     *
     * @param v the vertex to check for existence
     * @return true if the node exists, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isExistNode(Vertex<V> v){
        for(Vertex<V> vv : vertexNodes.keySet()){
            if(vv == v){
                return true;
            }
        }
        return false;
    }



    /**
     * Checks if a specified edge exists in the graph.
     *
     * @param e the edge to check for existence
     * @return true if the edge exists, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isExistEdge(Edge<E,V> e){
        for(Edge<E,V> ee : edgeNodes.keySet()){
            if(ee == e){
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if an edge exists between two specified vertices.
     *
     * @param v2 the outbound vertex
     * @param v1 the inbound vertex
     * @return true if the edge exists, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isExistEdge(Vertex<V> v2, Vertex<V> v1){
        //search edge
        for (Edge<E,V> e : edgeNodes.keySet()) {
            if(
                    edgeNodes.get(e).getInbound() == vertexNodes.get(v1) &&                        //edge with inbound node
                            edgeNodes.get(e).getOutbound() == vertexNodes.get(v2) &&               //edge with outbound node
                            edgeNodes.get(e).getDirection() != Constants.BIDIRECTIONAL    //edge not bidirectional
            ){
                return true;            //exist edge
            }
        }
        return false;                   //edge not found
    }


    /**
     * Checks if an edge needs to be renamed based on the specified vertex characters.
     *
     * @param v1 the first vertex character
     * @param v2 the second vertex character
     * @return true if the edge is to be renamed, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isEdgeToRename(char v1,char v2){
        try {
            SmartGraphVertexNode<V> vb1 = getNodeBase(v1);
            SmartGraphVertexNode<V> vb2 = getNodeBase(v2);
            for (Edge<E, V> e : edgeNodes.keySet()) {
                if(!isDoubleEdge(e) && isConnectedEdge(edgeNodes.get(e),vb2,vb1) ) {
                    return true;
                }else{
                    if(isEqualEdge(edgeNodes.get(e),vb2,vb1) ) {
                        return true;
                    }
                }
            }
        }catch (Exception ignored){

        }
        return false;
    }


    /**
     * Checks if the specified edge, based on the given edge DTO, needs to be renamed.
     *
     * @param edge the EdgeDTO representing the edge to check for renaming
     * @return {@code true} if the edge needs to be renamed; {@code false} otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isEdgeToRename(EdgeDTO edge){
        return isEdgeToRename(edge.getFrom(), edge.getTo());
    }

    /**
     * Checks if the edge between the specified starting and ending nodes is eligible for renaming.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return true if the edge can be renamed, false otherwise
     */
    public boolean isEdgeToRename(NodeDTO start, NodeDTO end){
        return isEdgeToRename(start.getLabel(), end.getLabel());
    }

    /**
     * Checks if an edge exists between two specified vertex characters.
     *
     * @param start the outbound vertex character
     * @param end the inbound vertex character
     * @return true if the edge exists, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistEdge(char start, char end){
        if(isDoubleEdge(start,end)){
            return isConnectedEdge(start,end);
        }else{
            Edge<E,V> e = getSpecificEdge(start, end);
            return e != null && isEqualEdge(edgeNodes.get(e),end,start);
        }
    }

    /**
     * Checks if an edge exists between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return true if the edge exists, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistEdge(NodeDTO start, NodeDTO end){
        return isExistEdge(start.getLabel(), end.getLabel());

    }






    /**
     * Checks if an edge between the specified nodes exists in the graph.
     *
     * @param edge the {@link EdgeDTO} object containing the details of the edge to check for existence,
     *             including the labels of the starting and ending nodes
     * @return true if the edge exists in the graph, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistEdge(EdgeDTO edge) {
        return isExistEdge(edge.getFrom(), edge.getTo());
    }



























    /**
     * Retrieves the last element from the vertex nodes based on their order.
     *
     * @return the character of the last vertex label
     * @throws NullPointerException if there are no vertex nodes
     *
     * @author vittoriopiotti
     */
    public char getLastElement() {
        return Objects.requireNonNull(vertexNodes.values()
                        .stream()
                        .reduce((first, second) -> second)
                        .orElse(null))
                .getAttachedLabel()
                .getText()
                .charAt(0);
    }






    /**
     * Sets the graph structure using a provided GraphDTO object, deleting all existing nodes first.
     *
     * @param pseudoGraph the GraphDTO object containing the new graph data
     *
     * @author vittoriopiotti
     */
    public void setGraph(GraphDTO pseudoGraph){
        if (!vertexNodes.isEmpty()) {
            do {
                deleteNode(getLastElement());
            } while (!vertexNodes.isEmpty());
        }
        for (NodeDTO node : pseudoGraph.getNodes()) {
            newNode( node.getLabel());
        }
        for (EdgeDTO edge : pseudoGraph.getEdges()) {
            newEdge(edge.getFrom() , edge.getTo(), edge.getCost(), edge.getIsArrowed());

        }
    }

    /**
     * Sets the graph structure using a provided GraphDTO object, with an option for animation.
     *
     * @param pseudoGraph the GraphDTO object containing the new graph data
     * @param isAnimated indicates if the transition should be animated
     *
     * @author vittoriopiotti
     */
    public void setGraph(GraphDTO pseudoGraph, boolean isAnimated){
        if(isAnimated) {

                adjustPosition.handle();
                double currentScaleX = this.getScaleX();
                double currentScaleY = this.getScaleY();
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.7), this);
                scaleTransition.setFromX(currentScaleX);
                scaleTransition.setFromY(currentScaleY);
                scaleTransition.setToX(0.0);
                scaleTransition.setToY(0.0);
                scaleTransition.setOnFinished(event -> {
                    setGraph(pseudoGraph);
                    ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.5), this);
                    scaleTransition2.setFromX(0.0);
                    scaleTransition2.setFromY(0.0);
                    scaleTransition2.setToX(1.0);
                    scaleTransition2.setToY(1.0);
                    scaleTransition2.play();
                });
                scaleTransition.play();
        }else{
                setGraph(pseudoGraph);
        }

    }









    /**
     * Generates a unique ID for an edge consisting of 4 random alphanumeric characters.
     * The method checks for existing edge IDs to ensure uniqueness.
     *
     * @return a unique edge ID
     *
     * @author vittoriopiotti
     */
    private String generateIdEdge() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String id;
        boolean idExists;
        do {
            StringBuilder idBuilder = new StringBuilder(4);
            for (int i = 0; i < 4; i++) {
                int index = random.nextInt(characters.length());
                idBuilder.append(characters.charAt(index));
            }
            id = idBuilder.toString();
            idExists = false;
            for (Edge<E, V> edge : edgeNodes.keySet()) {
                if (id.equals(edge.element())) {
                    idExists = true;
                    break;
                }
            }
        } while (idExists);
        return id;
    }


    /**
     * Returns the {@code Vertex<V>} node that has an associated label
     * whose first character matches the specified character.
     *
     * @param c the character to search for at the beginning of the label associated with the node
     * @return the {@code Vertex<V>} node whose associated label's first character matches
     *         the specified character, or {@code null} if no matching node is found
     * @throws NullPointerException if the label associated with a node or the text of the label is {@code null}
     */
    private Vertex<V> getNode(char c){
        for(Vertex<V> v : vertexNodes.keySet()){
            if(vertexNodes.get(v).getAttachedLabel().getText().charAt(0) == c){
                return v;
            }
        }
        return null;
    }

    /**
     * Checks if a specified vertex is active.
     * The method removes the "vertex-deactivated" style class if it exists,
     * indicating the vertex is now active. If the "vertex-deactivated-first" style class exists,
     * it will be removed, and the vertex will be marked as deactivated first.
     *
     * @param c the character of label vertex to check
     * @return true if the vertex is active, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isActiveNode(char c){
        try {
            Vertex<V> v = getNode(c);
            if(v != null){
                return isActiveNode(v);
            }
            return false;
        }catch(Exception ignored){
            return false;
        }
    }

    /**
     * Checks if the specified node is active in the graph.
     *
     * @param node the {@link NodeDTO} object containing the label of the node to check for activity
     * @return true if the node is active in the graph, false otherwise
     */
    public boolean isActiveNode(NodeDTO node){
       return isActiveNode(node.getLabel());
    }


    /**
     * Checks if a specified vertex is active.
     * The method removes the "vertex-deactivated" style class if it exists,
     * indicating the vertex is now active. If the "vertex-deactivated-first" style class exists,
     * it will be removed, and the vertex will be marked as deactivated first.
     *
     * @param v the vertex to check
     * @return true if the vertex is active, false otherwise
     *
     * @author vittoriopiotti
     */
    private boolean isActiveNode(Vertex<V> v){

            SmartGraphVertexNode<V> vertexNode = vertexNodes.get(v);

            if(vertexNode.removeStyleClass("vertex-deactivated")){
                vertexNode.addStyleClass("vertex-deactivated");
                return false;
            }else if(vertexNode.removeStyleClass("vertex-deactivated-first")){
                vertexNode.addStyleClass("vertex-deactivated-first");
                return false;
            }else{
                return true;
            }
    }

    /**
     * Activates all vertex nodes and edges in the graph.
     *
     * @author vittoriopiotti
     */
    public void activeAll(){
        activeAllNodes();
        activeAllEdges();
    }

    /**
     * Activates all vertex nodes in the graph by adding a specific style class.
     *
     * @author vittoriopiotti
     */
    public void activeAllNodes(){
        for (Vertex<V> v : vertexNodes.keySet()) {
            addStyleClassHandler(v,"vertex");

        }
    }

    /**
     * Activates all edges in the graph, resetting their styles.
     *
     * @author vittoriopiotti
     */
    public void activeAllEdges(){
        for (Edge<E, V> e : edgeNodes.keySet()) {
            edgeNodes.get(e).resetStyle();
        }
    }




    /**
     * Adds a specified style class to a vertex if it does not already have it.
     *
     * @param param the vertex to which the style class will be added
     * @param style the style class to add
     *
     * @author vittoriopiotti
     */
    private void addStyleClassHandler(Vertex<V> param,String style) {
        SmartGraphVertexNode<V> otherVertex = vertexNodes.get(param);
        if (!otherVertex.getStyleClass().contains(style)) {
            otherVertex.setStyleClass(style);
        }
    }


    /**
     * Retrieves the edge between the specified start and end nodes.
     * <br>
     * This method searches for an edge connecting the given nodes based on the
     * specified direction and edge configuration (unidirectional or bidirectional).
     *
     * @param start the start node as a character.
     * @param end the end node as a character.
     * @return the {@code Edge<E, V>} connecting the start and end nodes, or {@code null} if no such edge is found.
     */
    private Edge<E,V> _getEdge(char start,char end){
        for(Edge<E,V> e : edgeNodes.keySet()){
            SmartGraphEdgeBase<E,V> eb = edgeNodes.get(e);
            if(isDoubleEdge(e)){
                if(e.getDirection() == Constants.NATURAL_DIRECTION){
                    if(isEqualEdge(eb,start,end)){
                        return e;
                    }
                }else{
                    if(isEqualEdge(getOppositeEdge(eb),start,end)){
                        return e;
                    }
                }

            }else{
                if(eb.getDirection() == Constants.BIDIRECTIONAL){
                    if(isEqualEdge(eb,start,end) || isEqualEdge(eb,end,start)){
                        return e;
                    }
                }else{
                    if(isEqualEdge(eb,start,end)){
                        return e;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Deactivates connections with a specified vertex node, updating styles accordingly.
     * This method will deactivate all edges and vertices in the graph and mark
     * the specified vertex as "vertex-deactivated-first". It also deactivates
     * other vertices connected to the specified vertex.
     *
     * @param label the vertex label to disable connections for
     * @return true if the connections were successfully disabled, false if the vertex does not exist
     *
     * @author vittoriopiotti
     */
    public boolean disabledConnectionsWithNode(char label){
        try {
            Vertex<V> v = getNode(label);
            if(v != null){
                return disabledConnectionsWithNode(v);
            }
            return false;
        }catch(Exception ignored){
            return false;
        }
    }

    /**
     * Checks if the connections with the specified node, based on the given node DTO, are disabled.
     *
     * @param node the NodeDTO representing the node to check for disabled connections
     * @return {@code true} if the connections with the specified node are disabled; {@code false} otherwise
     *
     * @author vittoriopiotti
     */
    public boolean disabledConnectionsWithNode(NodeDTO node){
        return disabledConnectionsWithNode(node.getLabel());
    }







    /**
     * Deactivates connections with a specified vertex node, updating styles accordingly.
     * This method will deactivate all edges and vertices in the graph and mark
     * the specified vertex as "vertex-deactivated-first". It also deactivates
     * other vertices connected to the specified vertex.
     *
     * @param param the vertex to disable connections for
     * @return true if the connections were successfully disabled, false if the vertex does not exist
     *
     * @author vittoriopiotti
     */
    private boolean disabledConnectionsWithNode(Vertex<V> param){
        if(isExistNode(param)) {
            activeAllEdges();
            activeAllNodes();
            addStyleClassHandler(param, "vertex-deactivated-first");
            List<Vertex<V>> appList = listOfVertices();
            appList.remove(param);
            for (Vertex<V> vertex : appList) {
                if (getTotalEdgesBetween(param, vertex) > 0) {
                    addStyleClassHandler(vertex, "vertex-deactivated");
                }


            }
            return true;
        }
        return false;

    }



    /**
     * Retrieves the opposite edge of a given edge node by comparing inbound
     * and outbound labels with other edges in the graph.
     *
     * @param edgeNode the edge node for which to find the opposite edge
     * @return the opposite edge if found, or null if none exists
     *
     * @author vittoriopiotti
     */
    private SmartGraphEdgeBase<E, V> getOppositeEdge(SmartGraphEdgeBase<E, V> edgeNode){
        String inbound = edgeNode.getInbound().getAttachedLabel().getText();
        String outbound = edgeNode.getOutbound().getAttachedLabel().getText();
        SmartGraphEdgeBase<E, V> oppositeEdge = null;
        for (SmartGraphEdgeBase<E, V> otherEdge : edgeNodes.values()) {
            if(inbound.equals(otherEdge.getOutbound().getAttachedLabel().getText()) &&outbound.equals(otherEdge.getInbound().getAttachedLabel().getText()) ){
                oppositeEdge = otherEdge;
            }
        }
        return  oppositeEdge;
    }


    /**
     * Creates a background rectangle for the specified label and edge node.
     * The rectangle is styled with rounded corners and opacity.
     * Mouse event handlers are attached to the rectangle to manage interactions.
     *
     * @param label the label associated with the edge
     * @param edgeNode the edge node for which the background is created
     * @return a styled rectangle serving as a background for the label
     * @throws ClassCastException if edgeNode cannot be cast to the expected type
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    private Rectangle createBackground(SmartLabel label, SmartGraphEdgeBase<E,V> edgeNode){
        Rectangle background = new Rectangle(0, 0, labelBackground);
        background.setArcWidth(labelCornerRadius);
        background.setArcHeight(labelCornerRadius);
        background.setOpacity(labelOpacity);

        background.setOnMouseEntered(event -> handleMouseEntered(event, (SmartGraphEdgeBase<String, String>) edgeNode , label, edgeNode.getUnderlyingEdge()));
        background.setOnMouseExited(event -> handleMouseExited(event, (SmartGraphEdgeBase<String, String>) edgeNode, label, edgeNode.getUnderlyingEdge()));
        background.setOnMouseClicked(event -> _onClickEdge.accept(event, edgeNode.getUnderlyingEdge()));
        return  background;
    }

    /**
     * Sets a listener on the given label to update the background rectangle
     * position and size when the label's bounds change.
     *
     * @param label the label whose bounds will be monitored
     * @param background the rectangle background to be updated
     *
     * @author vittoriopiotti
     */
    private void setEdgeLabelListener(SmartLabel label, Rectangle background){
        label.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            background.setTranslateX(label.getBoundsInParent().getMinX() - (labelPaddingHorizontal / 2));
            background.setTranslateY(label.getBoundsInParent().getMinY() - (labelPaddingVertical / 2));
            background.setWidth(newBounds.getWidth() + labelPaddingHorizontal);
            background.setHeight(newBounds.getHeight() + labelPaddingVertical);
        });
    }




    /**
     * Handles mouse press events to initialize drag operations.
     * Records the initial mouse position and sets the dragging state to false.
     *
     * @param event the mouse event triggered on press
     *
     * @author vittoriopiotti
     */
    private void onMousePressed(MouseEvent event) {
        startX = event.getSceneX();
        startY = event.getSceneY();
        isDragging = false;
    }

    /**
     * Handles mouse drag events to determine if the mouse is being dragged
     * beyond a defined threshold. Updates the dragging state accordingly.
     *
     * @param event the mouse event triggered during drag
     *
     * @author vittoriopiotti
     */
    private void onMouseDragged(MouseEvent event) {
        double dragThreshold = 5.0;
        if (Math.abs(event.getSceneX() - startX) > dragThreshold ||
                Math.abs(event.getSceneY() - startY) > dragThreshold) {
            isDragging = true;
        }
    }

    /**
     * Handles mouse click events, distinguishing between a click and a drag.
     * If the mouse was not dragged, it triggers the background click action.
     *
     * @param event the mouse event triggered on click
     *
     * @author vittoriopiotti
     */
    private void onMouseClicked(MouseEvent event) {
        if (!isDragging) {
            onClickBackground.handle(event);
        }
    }


    /**
     * Finds an optimal point for placing a new vertex in the graph, ensuring
     * it is sufficiently distant from existing nodes.
     * The optimal point is selected based on the distance to existing node positions.
     *
     * @return a Point2D representing the optimal coordinates for a new vertex,
     *         or null if no valid position is found
     *
     * @author vittoriopiotti
     */
    private Point2D findOptimalPoint() {
        List<Point2D> positions = new ArrayList<>();
        Point2D optimalPoint = null;
        for (Map.Entry<Vertex<V>, SmartGraphVertexNode<V>> entry : vertexNodes.entrySet()) {
            SmartGraphVertexNode<V> node = entry.getValue();
            positions.add(node.getPosition());
        }
        if(positions.isEmpty()){
            Bounds bounds = getPlotBounds();
            double mx = bounds.getMinX() + bounds.getWidth() / 2.0;
            double my = bounds.getMinY() + bounds.getHeight() / 2.0;
            optimalPoint = new Point2D(mx, my);
        }
        if(positions.size() == 1){
            optimalPoint = new Point2D(positions.get(0).getX()+100, positions.get(0).getY()+100);
        }else{
            double minX = 0.0;
            double maxX = this.getWidth();
            double minY = 0.0;
            double maxY = this.getHeight();
            double minTotalDistance = Double.MAX_VALUE;
            for (double x = minX; x <= maxX; x += 1) {
                for (double y = minY; y <= maxY; y += 1) {
                    Point2D testPoint = new Point2D(x, y);
                    boolean isValid = true;
                    double totalDistance = 0;
                    for (Point2D pos : positions) {
                        double distance = testPoint.distance(pos);
                        if (distance < 100) {
                            isValid = false;
                            break;
                        }
                        totalDistance += distance;
                    }
                    if (isValid && totalDistance < minTotalDistance) {
                        minTotalDistance = totalDistance;
                        optimalPoint = testPoint;
                    }
                }
            }
        }
        return optimalPoint;
    }


    /**
     * Handles the mouse entered event for a graph edge, applying a hover style.
     *
     * @param event  the mouse event that triggered this method
     * @param graphEdge  the graph edge to apply the hover style to
     * @param label  the label associated with the graph edge
     * @param edge  the underlying edge object
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unused")
    private void handleMouseEntered(MouseEvent event, SmartGraphEdgeBase<String, String> graphEdge, SmartLabel label, Edge<E, V> edge) {
        // Apply hover style to the graph edge
        graphEdge.applyHoverStyle();

    }

    /**
     * Handles the mouse exited event for a graph edge, reverting to the default style.
     *
     * @param event  the mouse event that triggered this method
     * @param graphEdge  the graph edge to revert the style for
     * @param label  the label associated with the graph edge
     * @param edge  the underlying edge object
     *
     * @author vittoriopiotti
     */

    @SuppressWarnings("unused")
    private void handleMouseExited(MouseEvent event, SmartGraphEdgeBase<String, String> graphEdge, SmartLabel label, Edge<E, V> edge) {
        // Revert to default style or perform other actions
        graphEdge.applyDefaultStyle();

    }









    /**
     * Gets the stylable node associated with the specified edge.
     *
     * @param start start node of the edge for which to get the stylable node.
     * @param end end node of the edge for which to get the stylable node.
     * @return the stylable node representing the edge.
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private SmartStylableNode getStylableEdge(char start,char end) {
        return getStylableEdge(getSpecificEdge(start,end));
    }

    /**
     * Gets the stylable node associated with the specified vertex.
     *
     * @param v the vertex label for which to get the stylable node.
     * @return the stylable node representing the vertex.
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private SmartStylableNode getStylableVertex(char v){
        return getStylableVertex(getNode(v));
    }














    /**
     * Updates the view model by removing, inserting, and updating nodes.
     *
     * @author brunomnsilva
     */
    public synchronized void updateViewModel() {
        removeNodes();
        insertNodes();
        updateNodes();
    }










    /**
     * Removes a specified vertex and its associated edges from the graph.
     *
     * @param vertexToRemove the vertex to remove
     * @throws IllegalArgumentException if the vertex is not found
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void removeNode(Vertex<V> vertexToRemove) {
        if (vertexToRemove == null || !vertexNodes.containsKey(vertexToRemove)) {
            return;
        }
        List<Edge<E, V>> edgesToRemove = new ArrayList<>();
        for (Edge<E, V> edge : connections.keySet()) {
            Tuple<Vertex<V>> vertexTuple = connections.get(edge);

            if (vertexTuple.first.equals(vertexToRemove) || vertexTuple.second.equals(vertexToRemove)) {
                edgesToRemove.add(edge);
            }
        }

        for (Edge<E, V> e : edgesToRemove) {
            SmartGraphEdgeBase<E, V> edgeToRemove = edgeNodes.get(e);
            if (edgeToRemove != null) {
                edgeNodes.remove(e);
                removeEdge(edgeToRemove);
                Tuple<Vertex<V>> vertexTuple = connections.get(e);
                if (getTotalEdgesBetween(vertexTuple.first, vertexTuple.second) == 0) {
                    SmartGraphVertexNode<V> v0 = vertexNodes.get(vertexTuple.first);
                    SmartGraphVertexNode<V> v1 = vertexNodes.get(vertexTuple.second);

                    if (v0 != null) {
                        v0.removeAdjacentVertex(v1);
                    }
                    if (v1 != null) {
                        v1.removeAdjacentVertex(v0);
                    }
                }
                connections.remove(e);
            }
        }
        SmartGraphVertexNode<V> removed = vertexNodes.remove(vertexToRemove);
        if (removed != null) {
            removeVertex(removed);
        }
    }



    /**
     * Initializes nodes by creating graphical representations for vertices and edges.
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void initNodes() {

        /* create vertex graphical representations */
        for (Vertex<V> vertex : listOfVertices()) {

            SmartGraphVertexNode<V> vertexAnchor = createVertex(vertex, 0, 0);

            vertexNodes.put(vertex, vertexAnchor);
        }

        /* create edges graphical representations between existing vertices */
        //this is used to guarantee that no duplicate edges are ever inserted
        List<Edge<E, V>> edgesToPlace = listOfEdges();

        for (Vertex<V> vertex : vertexNodes.keySet()) {

            Iterable<Edge<E, V>> incidentEdges = theGraph.incidentEdges(vertex);

            for (Edge<E, V> edge : incidentEdges) {

                //if already plotted, ignore edge.
                if (!edgesToPlace.contains(edge)) {
                    continue;
                }

                Vertex<V> oppositeVertex = theGraph.opposite(vertex, edge);

                SmartGraphVertexNode<V> graphVertexIn = vertexNodes.get(vertex);
                SmartGraphVertexNode<V> graphVertexOppositeOut = vertexNodes.get(oppositeVertex);

                graphVertexIn.addAdjacentVertex(graphVertexOppositeOut);
                graphVertexOppositeOut.addAdjacentVertex(graphVertexIn);

                SmartGraphEdgeBase<E,V> graphEdge = createEdge(edge, graphVertexIn, graphVertexOppositeOut,edge.getCost(),edge.getDirection());


                /* Track Edges already placed */
                connections.put(edge, new Tuple<>(vertex, oppositeVertex));
                addEdge(graphEdge, edge);

                configureDiagraph(graphEdge);

                edgesToPlace.remove(edge);
            }
        }

        for (Vertex<V> vertex : vertexNodes.keySet()) {
            SmartGraphVertexNode<V> v = vertexNodes.get(vertex);
            addVertex(v);
        }
    }


    /**
     * Creates a vertex graphical representation.
     *
     * @param v the vertex to create
     * @param x the x-coordinate of the vertex
     * @param y the y-coordinate of the vertex
     * @return the created SmartGraphVertexNode
     *
     * @author brunomnsilva
     */
    private SmartGraphVertexNode<V> createVertex(Vertex<V> v, double x, double y) {
        // Read shape type from annotation or use default (circle)
        String shapeType = getVertexShapeTypeFor(v.element());
        double shapeRadius = getVertexShapeRadiusFor(v.element());
        return new SmartGraphVertexNode<>(v, x, y, shapeRadius, shapeType, graphProperties.getVertexAllowUserMove(),_onClickNode,closeContextMenu);
    }

    /**
     * Creates an edge graphical representation between two vertices.
     *
     * @param edge the edge to create
     * @param graphVertexInbound the inbound vertex node
     * @param graphVertexOutbound the outbound vertex node
     * @param cost the cost associated with the edge
     * @param direction the direction of the edge
     * @return the created SmartGraphEdgeBase
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private SmartGraphEdgeBase<E,V> createEdge(Edge<E, V> edge, SmartGraphVertexNode<V> graphVertexInbound, SmartGraphVertexNode<V> graphVertexOutbound, int cost, int direction) {
        /*
        Even if edges are later removed, the corresponding index remains the same. Otherwise, we would have to
        regenerate the appropriate edges.
         */
        int edgeIndex = 0;
        Integer counter = placedEdges.get(new Tuple<>(graphVertexInbound, graphVertexOutbound));
        if (counter != null) {
            edgeIndex = counter;
        }

        SmartGraphEdgeBase<E,V> graphEdge;

        //

        if (getTotalEdgesBetween(graphVertexInbound.getUnderlyingVertex(), graphVertexOutbound.getUnderlyingVertex()) > 1
                || graphVertexInbound == graphVertexOutbound) {
            graphEdge = new SmartGraphEdgeCurve<>(edge, graphVertexInbound, graphVertexOutbound, edgeIndex, cost,direction);
        } else {
            graphEdge = new SmartGraphEdgeLine<>(edge, graphVertexInbound, graphVertexOutbound, cost,direction);
        }


        //

        placedEdges.put(new Tuple<>(graphVertexInbound, graphVertexOutbound), ++edgeIndex);


        return graphEdge;
    }

    /**
     * Adds a vertex to the graphical representation.
     *
     * @param v the vertex to add
     * @throws IllegalArgumentException if the vertex is null
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void addVertex(SmartGraphVertexNode<V> v) {
        this.getChildren().add(v);

        String labelText = getVertexLabelFor(v.getUnderlyingVertex().element());

        if (graphProperties.getUseVertexTooltip()) {
            Tooltip t = new Tooltip(labelText);
            Tooltip.install(v, t);
        }

        if (graphProperties.getUseVertexLabel()) {
            SmartLabel label = new SmartLabel(labelText);
            label.addStyleClass("vertex-label");
            this.getChildren().add(label);
            v.attachLabel( label);
            label.setMouseTransparent(true);

        }
    }




    /**
     * Adds a new edge to the graph panel and updates the relevant data structures.
     *
     * @param e  the graph edge base to add
     * @param edge  the underlying edge object
     * @throws IllegalArgumentException if the edge is null
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void addEdge(SmartGraphEdgeBase<E, V> e, Edge<E, V> edge) {
        // Add edge to the back
        this.getChildren().add(0, (Node) e);
        edgeNodes.put(edge, e);

        String labelText = getEdgeLabelFor(edge.element());

        if (graphProperties.getUseEdgeTooltip()) {
            Tooltip t = new Tooltip(labelText);
            Tooltip.install((Node) e, t);
        }

        if (graphProperties.getUseEdgeLabel()) {
            SmartLabel label = new SmartLabel(labelText);
            label.addStyleClass("edge-label");


            label.setTranslateX(0);
            label.setTranslateY(0);
            label.setMouseTransparent(true);
            e.attachLabel(label);
            e.attachBackground(createBackground(label,e));
            this.getChildren().add(e.getAttachedBackground());
            this.getChildren().add(label);
            setEdgeLabelListener(label,e.getAttachedBackground());
        }
    }




    /**
     * Inserts nodes into the graph panel based on unplotted vertices and edges.
     *
     * @throws IllegalStateException if the graph is not initialized properly
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void insertNodes() {
        Collection<Vertex<V>> unplottedVertices = unplottedVertices();
        List<SmartGraphVertexNode<V>> newVertices = null;
        if (!unplottedVertices.isEmpty()) {
            newVertices = new LinkedList<>();
            for (Vertex<V> vertex : unplottedVertices) {

                Point2D optimalPoint = vertexNodes.isEmpty() ? new Point2D(this.getPlotBounds().getMinX() + this.getPlotBounds().getWidth()/2.0+50, this.getPlotBounds().getMinY() + this.getPlotBounds().getHeight() / 2.0+50) : findOptimalPoint();
                SmartGraphVertexNode<V> newVertex = createVertex(vertex, optimalPoint.getX(), optimalPoint.getY());
                newVertices.add(newVertex);
                vertexNodes.put(vertex, newVertex);

            }
        }

        Collection<Edge<E, V>> unplottedEdges = unplottedEdges();
        if (!unplottedEdges.isEmpty()) {
            for (Edge<E, V> edge : unplottedEdges) {
                Vertex<V>[] vertices = edge.vertices();
                Vertex<V> u = vertices[0]; //outbound if digraph, by javadoc requirement
                Vertex<V> v = vertices[1]; //inbound if digraph, by javadoc requirement

                SmartGraphVertexNode<V> graphVertexOut = vertexNodes.get(u);
                SmartGraphVertexNode<V> graphVertexIn = vertexNodes.get(v);

                /*
                Updates may be coming too fast, and we can get out of sync.
                Skip and wait for another update call, since they will surely
                be coming at this pace.
                */
                if(graphVertexIn == null || graphVertexOut == null) {
                    continue;
                }

                graphVertexOut.addAdjacentVertex(graphVertexIn);
                graphVertexIn.addAdjacentVertex(graphVertexOut);

                SmartGraphEdgeBase<E,V> graphEdge = createEdge(edge, graphVertexIn, graphVertexOut,edge.getCost(),edge.getDirection());


                configureDiagraph(graphEdge);
                 /* Track edges */
                connections.put(edge, new Tuple<>(u, v));
                addEdge(graphEdge, edge);

            }
        }

        if (newVertices != null) {
            for (SmartGraphVertexNode<V> v : newVertices) {
                addVertex(v);
            }
        }

    }

    /**
     * Configures a directed graph edge with an arrow, setting up event handling.
     *
     * @param graphEdge  the graph edge to configure
     * @throws NullPointerException if the graphEdge is null
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void configureDiagraph(SmartGraphEdgeBase<E,V> graphEdge){
        if (this.edgesWithArrows && theGraph instanceof Digraph) {
            SmartArrow arrow = new SmartArrow(this.graphProperties.getEdgeArrowSize(),arrowStyle);
            graphEdge.setDirection(graphEdge.getUnderlyingEdge().getDirection());
            graphEdge.attachArrow(arrow);
            arrow.setOnMouseClicked(event -> _onClickEdge.accept(event, graphEdge.getUnderlyingEdge()));
            graphEdge.setOnMouseClicked_(event -> _onClickEdge.accept(event, graphEdge.getUnderlyingEdge()));
            if(graphEdge.getDirection() == Constants.BIDIRECTIONAL){
                this.getChildren().remove(graphEdge.getAttachedArrow());
            }else{
                if(!this.getChildren().contains(graphEdge.getAttachedArrow())  ){
                    this.getChildren().add(graphEdge.getAttachedArrow());
                }
            }
        }
    }

    /**
     * Removes nodes and edges from the graph panel that have been removed from the underlying graph.
     *
     * @throws IllegalStateException if there is an issue with the graph's state
     *
     * @author brunomnsilva
     */
    private void removeNodes() {
         //remove edges (graphical elements) that were removed from the underlying graph
        Collection<Edge<E, V>> removedEdges = removedEdges();
        for (Edge<E, V> e : removedEdges) {
            SmartGraphEdgeBase<E,V> edgeToRemove = edgeNodes.get(e);
            edgeNodes.remove(e);
            removeEdge(edgeToRemove);   //remove from panel

            //when edges are removed, the adjacency between vertices changes
            //the adjacency is kept in parallel in an internal data structure
            Tuple<Vertex<V>> vertexTuple = connections.get(e);

            if( getTotalEdgesBetween(vertexTuple.first, vertexTuple.second) == 0 ) {
                SmartGraphVertexNode<V> v0 = vertexNodes.get(vertexTuple.first);
                SmartGraphVertexNode<V> v1 = vertexNodes.get(vertexTuple.second);

                v0.removeAdjacentVertex(v1);
                v1.removeAdjacentVertex(v0);
            }

            connections.remove(e);
        }

        //remove vertices (graphical elements) that were removed from the underlying graph
        Collection<Vertex<V>> removedVertices = removedVertices();
        for (Vertex<V> removedVertex : removedVertices) {
            SmartGraphVertexNode<V> removed = vertexNodes.remove(removedVertex);
            removeVertex(removed);
        }

    }




    /**
     * Removes the specified edge from the graph panel.
     *
     * @param e the edge to remove
     *
     * @author brunomnsilva
     */
    private void removeEdge(SmartGraphEdgeBase<E, V> e) {
        getChildren().remove((Node) e);
        SmartArrow attachedArrow =  e.getAttachedArrow();
        if (attachedArrow != null) {
            getChildren().remove(attachedArrow);
        }
        Text attachedLabel = e.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
        getChildren().remove(e.getAttachedBackground());
    }

    /**
     * Removes the specified vertex from the graph panel.
     *
     * @param v the vertex to remove
     * @author brunomnsilva
     */
    private void removeVertex(SmartGraphVertexNode<V> v) {
        getChildren().remove(v);

        Text attachedLabel = v.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
    }

    /**
     * Updates node's labels
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void updateNodes() {
        theGraph.vertices().forEach((v) -> {
            SmartGraphVertexNode<V> vertexNode = vertexNodes.get(v);
            if (vertexNode != null) {
                SmartLabel label = vertexNode.getAttachedLabel();
                if(label != null) {
                    String text = getVertexLabelFor(v.element());
                    label.setText_( text );
                }
                double radius = getVertexShapeRadiusFor(v.element());
                vertexNode.setRadius(radius);
                String shapeType = getVertexShapeTypeFor(v.element());
                vertexNode.setShapeType(shapeType);
            }
        });
        getChildren().removeIf(node -> node instanceof Rectangle);
        theGraph.edges().forEach((e) -> {
            SmartGraphEdgeBase<E,V> edgeNode = edgeNodes.get(e);
            if (edgeNode != null) {
                SmartLabel label =  edgeNode.getAttachedLabel();
                if (label != null) {
                    label.setText_( String.valueOf(edgeNode.getCost()) );
                }
            }
        });
        theGraph.edges().forEach((e) -> {
            SmartGraphEdgeBase<E, V> edgeNode = edgeNodes.get(e);
            SmartLabel label =  edgeNode.getAttachedLabel();
            edgeNode.attachBackground(createBackground(label,edgeNode));
            this.getChildren().remove(label);
            this.getChildren().add(edgeNode.getAttachedBackground());
            this.getChildren().add(label);
            setEdgeLabelListener(label,edgeNode.getAttachedBackground());
            edgeNode.setDirection(e.getDirection());
            if(edgeNode.getDirection() == Constants.BIDIRECTIONAL){
                this.getChildren().remove(edgeNode.getAttachedArrow());
            }else{
                if(!this.getChildren().contains(edgeNode.getAttachedArrow())  ){
                    this.getChildren().add(edgeNode.getAttachedArrow());
                }
            }
        });
    }



    /**
     * Retrieves the label for a given vertex element. If a label provider is set, it uses that; otherwise,
     * it checks for a method annotated with {@link SmartLabelSource} for the vertex element class.
     *
     * @param vertexElement the vertex element for which to retrieve the label
     * @return the label string for the vertex element
     * @throws SecurityException if a security violation occurs during method invocation
     * @throws IllegalArgumentException if the method argument is invalid
     *
     * @author brunomnsilva
     */
    protected final String getVertexLabelFor(V vertexElement) {

        if(vertexElement == null) return "<NULL>";

        if(vertexLabelProvider != null) {
            return vertexLabelProvider.valueFor(vertexElement);
        }

        try {
            Class<?> clazz = vertexElement.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartLabelSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(vertexElement);
                    return value.toString();
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return vertexElement.toString();
    }


    /**
     * Retrieves the label for a given edge element. If a label provider is set, it uses that; otherwise,
     * it checks for a method annotated with {@link SmartLabelSource} for the edge element class.
     *
     * @param edgeElement the edge element for which to retrieve the label
     * @return the label string for the edge element
     * @throws SecurityException if a security violation occurs during method invocation
     * @throws IllegalArgumentException if the method argument is invalid
     *
     * @author brunomnsilva
     */
    protected final String getEdgeLabelFor(E edgeElement) {

        if(edgeElement == null) return "<NULL>";

        if(edgeLabelProvider != null) {
            return edgeLabelProvider.valueFor(edgeElement);
        }

        try {
            Class<?> clazz = edgeElement.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartLabelSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(edgeElement);
                    return value.toString();
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException |InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return edgeElement.toString();
    }



    /**
     * Retrieves the shape type for a given vertex element. If the vertex element is null,
     * the default shape type is returned. If a shape type provider is set, it is used; otherwise,
     * the method checks for a method annotated with {@link SmartShapeTypeSource} in the vertex element's class.
     *
     * @param vertexElement the vertex element for which to retrieve the shape type
     * @return the shape type string for the vertex element
     * @throws SecurityException if a security violation occurs during method invocation
     * @throws IllegalArgumentException if the method argument is invalid
     * @author brunomnsilva
     */
    protected final String getVertexShapeTypeFor(V vertexElement) {

        if(vertexElement == null) return graphProperties.getVertexShape();

        if(vertexShapeTypeProvider != null) {
            return vertexShapeTypeProvider.valueFor(vertexElement);
        }

        try {
            Class<?> clazz = vertexElement.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartShapeTypeSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(vertexElement);
                    return value.toString();
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return graphProperties.getVertexShape();
    }


    /**
     * Retrieves the radius for a given vertex element. If the vertex element is null,
     * the default radius is returned. If a radius provider is set, it is used; otherwise,
     * the method checks for a method annotated with {@link SmartRadiusSource} in the vertex element's class.
     *
     * @param vertexElement the vertex element for which to retrieve the radius
     * @return the radius of the vertex element
     * @throws SecurityException if a security violation occurs during method invocation
     * @throws IllegalArgumentException if the method argument is invalid
     * @author brunomnsilva
     */
    protected final double getVertexShapeRadiusFor(V vertexElement) {

        if(vertexElement == null) return graphProperties.getVertexRadius();

        if(vertexRadiusProvider != null) {
            return vertexRadiusProvider.valueFor(vertexElement);
        }

        try {
            Class<?> clazz = vertexElement.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartRadiusSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(vertexElement);
                    return Double.parseDouble(value.toString());
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return graphProperties.getVertexRadius();
    }


    /**
     *
     * @author brunomnsilva
     */
    private synchronized void runAutomaticLayout() {
        for (int i = 0; i < AUTOMATIC_LAYOUT_ITERATIONS; i++) {
            resetForces();
            computeForces();
            updateForces();
        }
        applyForces();
    }


    /**
     * Runs the initial current vertex placement strategy.
     * <p>
     * This method should only be called once during the lifetime of the object
     * and only after the underlying {@link Scene} is displayed.
     * <br/>
     * Furthermore, required updates should be performed through the {@link #update()
     * } method.
     *
     * @throws IllegalStateException The exception is thrown if: (1) the Scene
     * is not yet displayed; (2) It has zero width and/or height, and; (3) If
     * this method was already called.
     *
     * @author brunomsilva
     */
    public void init() throws IllegalStateException {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        } else if (this.getWidth() == 0 || this.getHeight() == 0) {
            throw new IllegalStateException("The layout for this panel has zero width and/or height");
        } else if (this.initialized) {
            throw new IllegalStateException("Already initialized. Use update() method instead.");
        }

        if (placementStrategy != null) {
            // call strategy to place the vertices in their initial locations
            placementStrategy.place(this.widthProperty().doubleValue(),
                    this.heightProperty().doubleValue(),
                    this);
        } else {
            //apply circular placement, I think it's a better initial state for automatic layout
            new SmartCircularSortedPlacementStrategy().place(this.widthProperty().doubleValue(),
                    this.heightProperty().doubleValue(),
                    this);

            //start automatic layout
            timer.start();
        }


        this.getScene().getRoot().layout();
        for (Map.Entry<Edge<E, V>, SmartGraphEdgeBase<E, V>> entry3 : edgeNodes.entrySet()) {
            SmartGraphEdgeBase<E, V> graphEdgeBase2 = entry3.getValue();
            graphEdgeBase2.setCost(graphEdgeBase2.getCost());
        }

        this.initialized = true;

    }






    /**
     * Returns the property used to toggle the automatic layout of vertices.
     *
     * @return  automatic layout property
     *
     * @author brunomsilva
     */
    @SuppressWarnings("unused")
    private BooleanProperty automaticLayoutProperty() {
        return this.automaticLayoutProperty;
    }

    /**
     * Toggle the automatic layout of vertices.
     *
     * @param value     true if enabling; false, otherwise
     *
     * @author brunomsilva
     */
    public void setAutomaticLayout(boolean value) {
        automaticLayoutProperty.set(value);
    }

    /**
     * Changes the current automatic layout strategy.
     * @param strategy the new strategy to use
     *
     * @author brunomsilva
     */
    @SuppressWarnings("unused")
    private void setAutomaticLayoutStrategy(ForceDirectedLayoutStrategy<V> strategy) {
        Args.requireNotNull(strategy, "strategy");
        this.automaticLayoutStrategy = strategy;
    }

    /**
     * Returns the reference of underlying model depicted by this panel.
     * <br/> The concrete type of the returned instance may be a {@link Graph} or {@link Digraph}.
     *
     * @return the underlying model
     *
     * @author brunomsilva
     */
    private Graph<V, E> getModel() {
        return theGraph;
    }



    /**
     * Returns a collection of the smart vertices that represent the underlying model vertices.
     * @return a collection of the smart vertices
     *
     * @author brunomnsilva
     */
    protected final Collection<SmartGraphVertex<V>> getSmartVertices() {
        return new ArrayList<>(this.vertexNodes.values());
    }

    /**
     * Returns a collection of the smart edges that represent the underlying model edges.
     * @return a collection of the smart edges
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    protected final Collection<SmartGraphEdge<E, V>> getSmartEdges() {
        return new ArrayList<>(this.edgeNodes.values());
    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph, immediately returning to the caller.
     * <br/>
     * This method invokes the refresh in the graphical
     * thread through Platform.runLater(), so it's not guaranteed that the visualization is in sync
     * immediately after this method finishes. That is, this method
     * immediately returns to the caller without waiting for the update to the
     * visualization.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void update() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
            throw new IllegalStateException("You must call init() method before any updates.");
        }

        //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
        Platform.runLater(this::updateViewModel);
    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph and waits for completion of the update.
     * <br/>
     * Use this variant only when necessary, e.g., need to style an element
     * immediately after adding it to the underlying graph. Otherwise, use
     * {@link #update() } instead for performance’s sake.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void updateAndWait() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
            throw new IllegalStateException("You must call init() method before any updates.");
        }

        final FutureTask<Boolean> update = new FutureTask<>(() -> {
            updateViewModel();
            return true;
        });

        //
        if(!Platform.isFxApplicationThread()) {
            //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
            Platform.runLater(update);

            //wait for completion, only outside javafx thread; otherwise -> deadlock
            try {
                update.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            updateViewModel();
        }

    }



    /**
     * Sets the vertex label provider for this SmartGraphPanel.
     * <br/>
     * The label provider has priority over any other method of obtaining the same values, such as annotations.
     * <br/>
     * To remove the provider, call this method with a <code>null</code> argument.
     *
     * @param labelProvider the label provider to set
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void setVertexLabelProvider(SmartLabelProvider<V> labelProvider) {
        this.vertexLabelProvider = labelProvider;
    }

    /**
     * Sets the edge label provider for this SmartGraphPanel.
     * <br/>
     * The label provider has priority over any other method of obtaining the same values, such as annotations.
     * <br/>
     * To remove the provider, call this method with a <code>null</code> argument.
     *
     * @param labelProvider the label provider to set
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void setEdgeLabelProvider(SmartLabelProvider<E> labelProvider) {
        this.edgeLabelProvider = labelProvider;
    }

    /**
     * Sets the radius provider for this SmartGraphPanel.
     * <br/>
     * The radius provider has priority over any other method of obtaining the same values, such as annotations.
     * <br/>
     * To remove the provider, call this method with a <code>null</code> argument.
     *
     * @param vertexRadiusProvider the radius provider to set
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void setVertexRadiusProvider(SmartRadiusProvider<V> vertexRadiusProvider) {
        this.vertexRadiusProvider = vertexRadiusProvider;
    }

    /**
     * Sets the shape type provider for this SmartGraphPanel.
     * <br/>
     * The shape type provider has priority over any other method of obtaining the same values, such as annotations.
     * <br/>
     * To remove the provider, call this method with a <code>null</code> argument.
     *
     * @param vertexShapeTypeProvider the shape type provider to set
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void setVertexShapeTypeProvider(SmartShapeTypeProvider<V> vertexShapeTypeProvider) {
        this.vertexShapeTypeProvider = vertexShapeTypeProvider;
    }


    /**
     * Computes the bounding box from all displayed vertices.
     *
     * @return bounding box
     *
     * @author brunomnsilva
     */
    private Bounds getPlotBounds() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
                maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        if(vertexNodes.isEmpty()) return new BoundingBox(0, 0, getWidth(), getHeight());

        for (SmartGraphVertexNode<V> v : vertexNodes.values()) {
            minX = Math.min(minX, v.getCenterX());
            minY = Math.min(minY, v.getCenterY());
            maxX = Math.max(maxX, v.getCenterX());
            maxY = Math.max(maxY, v.getCenterY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Computes the forces on the vertices using the current layout strategy.
     * This method delegates the computation to the active layout strategy.
     *
     * @author brunomnsilva
     */
    private void computeForces() {
        // Delegate to current layout strategy
        automaticLayoutStrategy.computeForces(vertexNodes.values(), getWidth(), getHeight());
    }

    /**
     * Checks if two vertices are adjacent in the graph.
     *
     * @param v the first vertex
     * @param u the second vertex
     * @return true if vertex v is adjacent to vertex u, false otherwise
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private boolean areAdjacent(SmartGraphVertexNode<V> v, SmartGraphVertexNode<V> u) {
        return v.isAdjacentTo(u);
    }


    /**
     * Updates the force delta for all vertex nodes.
     * This method should be called after forces are computed.
     *
     * @author brunomnsilva
     */
    private void updateForces() {
        vertexNodes.values().forEach(SmartGraphVertexNode::updateDelta);
    }


    /**
     * Applies the computed forces to move the vertices.
     * This method should be called after updating the forces.
     *
     * @author brunomnsilva
     */
    private void applyForces() {
        vertexNodes.values().forEach(SmartGraphVertexNode::moveFromForces);
    }


    /**
     * Resets the forces on all vertex nodes to their initial state.
     *
     * @author brunomnsilva
     */
    private void resetForces() {
        vertexNodes.values().forEach(SmartGraphVertexNode::resetForces);
    }


    /**
     * Returns the total number of edges between two vertices in the graph.
     *
     * @param v the first vertex
     * @param u the second vertex
     * @return the total number of edges connecting vertex v and vertex u
     *
     * @author brunomnsilva
     */
    private int getTotalEdgesBetween(Vertex<V> v, Vertex<V> u) {
        int count = 0;
        for (Edge<E, V> edge : theGraph.edges()) {
            if (edge.vertices()[0] == v && edge.vertices()[1] == u
                    || edge.vertices()[0] == u && edge.vertices()[1] == v) {
                count++;
            }
        }
        return count;
    }

    /**
     * Lists all edges in the graph.
     *
     * @return a list of edges in the graph
     *
     * @author brunomnsilva
     */
    private List<Edge<E, V>> listOfEdges() {
        return new LinkedList<>(theGraph.edges());
    }

    /**
     * Lists all vertices in the graph.
     *
     * @return a list of vertices in the graph
     *
     * @author brunomnsilva
     */
    private List<Vertex<V>> listOfVertices() {
        return new LinkedList<>(theGraph.vertices());
    }

    /**
     * Computes the vertex collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of vertices
     *
     * @author brunomnsilva
     */
    private Collection<Vertex<V>> unplottedVertices() {
        List<Vertex<V>> unplotted = new LinkedList<>();

        for (Vertex<V> v : theGraph.vertices()) {
            if (!vertexNodes.containsKey(v)) {
                unplotted.add(v);
            }
        }

        return unplotted;
    }

    /**
     * Computes the collection for vertices that are currently being displayed but do
     * no longer exist in the underlying graph.
     *
     * @return collection of vertices
     *
     * @author brunomnsilva
     */
    private Collection<Vertex<V>> removedVertices() {
        List<Vertex<V>> removed = new LinkedList<>();

        Collection<Vertex<V>> graphVertices = theGraph.vertices();
        Collection<SmartGraphVertexNode<V>> plotted = vertexNodes.values();

        for (SmartGraphVertexNode<V> v : plotted) {
            if (!graphVertices.contains(v.getUnderlyingVertex())) {
                removed.add(v.getUnderlyingVertex());
            }
        }

        return removed;
    }

    /**
     * Computes the collection for edges that are currently being displayed but do
     * no longer exist in the underlying graph.
     *
     * @return collection of edges
     *
     * @author brunomnsilva
     */
    private Collection<Edge<E, V>> removedEdges() {
        List<Edge<E, V>> removed = new LinkedList<>();

        Collection<Edge<E, V>> graphEdges = theGraph.edges();
        Collection<SmartGraphEdgeBase<E,V>> plotted = edgeNodes.values();

        for (SmartGraphEdgeBase<E,V> e : plotted) {
            if (!graphEdges.contains(e.getUnderlyingEdge())) {
                removed.add(e.getUnderlyingEdge());
            }
        }

        return removed;
    }

    /**
     * Computes the edge collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of edges
     *
     * @author brunomnsilva
     */
    private Collection<Edge<E, V>> unplottedEdges() {
        List<Edge<E, V>> unplotted = new LinkedList<>();

        for (Edge<E, V> e : theGraph.edges()) {
            if (!edgeNodes.containsKey(e)) {
                unplotted.add(e);
            }
        }

        return unplotted;
    }

    /**
     * Sets a vertex position (its center) manually.
     * <br/>
     * The positioning should be inside the boundaries of the panel, but
     * no restrictions are enforced by this method, so be aware.
     *
     * @param v underlying vertex
     * @param x x-coordinate on panel
     * @param y y-coordinate on panel
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    public void setVertexPosition(Vertex<V> v, double x, double y) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            node.setPosition(x, y);
        }
    }

    /**
     * Return the current x-coordinate (relative to the panel) of a vertex.
     *
     * @param v underlying vertex
     * @return the x-coordinate or NaN if the vertex does not exist
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    public double getVertexPositionX(Vertex<V> v) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            return node.getPositionCenterX();
        }
        return Double.NaN;
    }

    /**
     * Return the current y-coordinate (relative to the panel) of a vertex.
     *
     * @param v underlying vertex
     * @return the y-coordinate or NaN if the vertex does not exist
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    public double getVertexPositionY(Vertex<V> v) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            return node.getPositionCenterY();
        }
        return Double.NaN;
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element
     *
     * @author brunomnsilva
     */
    public SmartStylableNode getStylableVertex(Vertex<V> v) {
        return vertexNodes.get(v);
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param vertexElement underlying vertex's element
     * @return stylable element
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    public SmartStylableNode getStylableVertex(V vertexElement) {
        for (Vertex<V> v : vertexNodes.keySet()) {
            if (v.element().equals(vertexElement)) {
                return vertexNodes.get(v);
            }
        }
        return null;
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edge underlying graph edge
     * @return stylable element
     *
     * @author brunomnsilva
     */
    private SmartStylableNode getStylableEdge(Edge<E, V> edge) {
        return edgeNodes.get(edge);
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edgeElement underlying graph edge's element
     * @return stylable element
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private SmartStylableNode getStylableEdge(E edgeElement) {
        for (Edge<E, V> e : edgeNodes.keySet()) {
            if (e.element().equals(edgeElement)) {
                return edgeNodes.get(e);
            }
        }
        return null;
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element (label)
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private SmartStylableNode getStylableLabel(Vertex<V> v) {
        SmartGraphVertexNode<V> vertex = vertexNodes.get(v);

        return vertex != null ? vertex.getStylableLabel() : null;
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param e underlying graph edge
     * @return stylable element (label)
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private SmartStylableNode getStylableLabel(Edge<E,V> e) {
        SmartGraphEdgeBase<E,V> edge = edgeNodes.get(e);

        return edge != null ? edge.getStylableLabel() : null;
    }


    /**
     * Loads the stylesheet and applies the .graph class to this panel.
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("unused")
    private void loadAndApplyStylesheet(URI cssFile) {

        String css = Objects.requireNonNull(getClass().getResource("/smartgraph.css")).toExternalForm();
        if (css != null) {
            this.getStylesheets().add(css);
        }
    }

    @SuppressWarnings("unused")
    private static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0) {
            return min;
        }

        if (Double.compare(value, max) > 0) {
            return max;
        }

        return value;
    }



    /**
     * Represents a tuple in Java.
     *
     * @param <T> the type of the tuple
     *
     * @author brunomnsilva
     */
    @SuppressWarnings("all")
    private static class Tuple<T> {

        private final T first;
        private final T second;

        public Tuple(T first, T second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.first);
            hash = 29 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tuple<?> other = (Tuple<?>) obj;
            if (!Objects.equals(this.first, other.first)) {
                return false;
            }
            return Objects.equals(this.second, other.second);
        }
    }

}

