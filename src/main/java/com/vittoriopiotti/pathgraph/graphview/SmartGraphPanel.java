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

import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import com.vittoriopiotti.pathgraph.constants.AppConstants;
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
import java.util.function.Consumer;
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
    private final Map<Vertex<V>, SmartGraphVertexNode<V>> vertexNodes;

    /**
     * A map that associates each edge with its visual representation (`SmartGraphEdgeBase`).
     */
    private final Map<Edge<E, V>, SmartGraphEdgeBase<E, V>> edgeNodes;

    /**
     * A map that associates each edge with its connected vertices.
     */
    private final Map<Edge<E, V>, Tuple<Vertex<V>>> connections;

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
    private final Runnable hideContextMenu;

    /**
     * Callback to adjust the position of nodes when they are dragged or moved.
     */
    private Runnable adjustPosition;

    /**
     * Callback invoked when an arrow (edge) is clicked, accepting the event and the associated edge.
     */
    private BiConsumer<MouseEvent, Edge<E, V>> onClickArrow;

    /**
     * Callback invoked when a node (vertex) is clicked, accepting the event and the associated vertex.
     */
    private BiConsumer<MouseEvent, Vertex<V>> onClickNode;

    /**
     * Callback invoked when the background is clicked, accepting the event.
     */
    private Consumer<MouseEvent> onClickBackground;

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
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
                           Runnable hideContextMenu,
                           BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
                           BiConsumer<MouseEvent, Vertex<V>> onClickNode,
                           Consumer<MouseEvent> onClickBackground,
                           Runnable adjustPosition

    ) {

        Args.requireNotNull(theGraph, "theGraph");
        Args.requireNotNull(properties, "properties");
        Args.requireNotNull(placementStrategy, "placementStrategy");
        Args.requireNotNull(cssFile, "cssFile");
        Args.requireNotNull(layoutStrategy, "layoutStrategy");
        this.hideContextMenu = hideContextMenu;
        this.onClickArrow = onClickArrow;
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
        this.vertexNodes = new HashMap<>();
        this.edgeNodes = new HashMap<>();
        this.connections = new HashMap<>();
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
     * @param theGraph the underlying graph to visualize
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            Graph<V, E> theGraph,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
    ) {
        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
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
     * @param theGraph the underlying graph to visualize
     * @param layoutStrategy the automatic layout strategy to use
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            ForceDirectedLayoutStrategy<V> layoutStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
    ) {
        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
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
     * @param theGraph the underlying graph to visualize
     * @param properties custom properties for the graph visualization
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
    ) {
        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
                onClickNode,
                onClickBackground,
                adjustPosition
        );
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties and styling from smartgraph.css.
     *
     * @param theGraph the underlying graph to visualize
     * @param placementStrategy the strategy used for placing vertices in the graph
     * @param layoutStrategy the automatic layout strategy to arrange graph elements
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW}, the arrows will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            SmartPlacementStrategy placementStrategy,
            ForceDirectedLayoutStrategy<V> layoutStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition

    ) {
        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
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
     * @param theGraph the underlying graph to visualize
     * @param placementStrategy the strategy used for placing vertices in the graph;
     *                          can be <code>null</code> for default placement
     * @param arrowStyle the style of the arrow: if equal to {@code ArrowSmart.ARROW},
     *                   it will be drawn without fill
     * @param labelPaddingHorizontal the horizontal padding for labels associated with graph nodes
     * @param labelPaddingVertical the vertical padding for labels associated with graph nodes
     * @param labelBackground the background color of the node labels
     * @param labelCornerRadius the corner radius for the background of the node labels
     * @param labelOpacity the opacity of the label background
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            SmartPlacementStrategy placementStrategy,
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
    ) {
        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
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
     * @param theGraph the underlying graph to visualize
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
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            int arrowStyle,
            double labelPaddingHorizontal,
            double labelPaddingVertical,
            Color labelBackground,
            double labelCornerRadius,
            double labelOpacity,
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
    ) {

        this(
                theGraph,
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
                hideContextMenu,
                onClickArrow,
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
     * @param hideContextMenu a callback to hide the context menu when triggered
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked, receiving the {@code MouseEvent} and the {@code Edge} clicked
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
            Runnable hideContextMenu,
            BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,
            BiConsumer<MouseEvent, Vertex<V>> onClickNode,
            Consumer<MouseEvent> onClickBackground,
            Runnable adjustPosition
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
                hideContextMenu,
                onClickArrow,
                onClickNode,
                onClickBackground,
                adjustPosition
        );

    }



    /**
     * Sets all the callback functions for handling mouse events on graph elements.
     *
     * @param onClickArrow a callback invoked when an edge (arrow) is clicked,
     *                     receiving the {@code MouseEvent} and the {@code Edge} clicked
     * @param onClickNode a callback invoked when a node (vertex) is clicked,
     *                    receiving the {@code MouseEvent} and the {@code Vertex} clicked
     * @param onClickBackground a callback invoked when the background is clicked,
     *                          receiving the {@code MouseEvent}
     * @param adjustPosition a callback to adjust the position of nodes when dragged or repositioned
     *
     * @author vittoriopiotti
     */
    public void setAllCallbacks(BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,BiConsumer<MouseEvent, Vertex<V>> onClickNode,Consumer<MouseEvent> onClickBackground,Runnable adjustPosition){
        this.onClickArrow = onClickArrow;
        this.onClickNode = onClickNode;
        this.onClickBackground = onClickBackground;
        this.adjustPosition = adjustPosition;
        setNodesListeners();
    }

    /**
     * Retrieves the labels of all nodes in the graph.
     *
     * @return a list of strings representing the labels of all nodes (vertices) in the graph
     *
     * @author vittoriopiotti
     */
    public List<String> getNodesLabel() {
        List<String> vertices = new ArrayList<>();
        for (Map.Entry<Vertex<V>, SmartGraphVertexNode<V>> entry : vertexNodes.entrySet()) {
            vertices.add(entry.getValue().getAttachedLabel().getText()); // Usa toString o un metodo simile per ottenere la rappresentazione desiderata
        }
        return vertices;
    }

    /**
     * Generates a new node label that is not already used by existing nodes.
     * <br>
     * The label will be the first available letter from A to Z that is not currently assigned to any node.
     * If all letters are used, an empty string is returned.
     *
     * @return the new node label, or an empty string if all letters are taken
     *
     * @author vittoriopiotti
     */
    public String getNewNodeLabel() {
        List<String> nodesLabel = getNodesLabel();
        List<String> alphabet = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        String newNodeLabel = "";
        if (nodesLabel.size() < alphabet.size()) {
            for (String letter : alphabet) {
                if (!nodesLabel.contains(letter)) {
                    newNodeLabel = letter;
                    break;
                }
            }
        }
        return newNodeLabel;
    }




    /**
     * Sets listeners for all vertex nodes in the graph.
     *
     * @author vittoriopiotti
     */
    private void setNodesListeners(){
        for (SmartGraphVertexNode<V> node : vertexNodes.values()) {
            node.setAllCallbacks(
                    hideContextMenu,
                    onClickNode
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
    public boolean renameEdge(Edge<E, V> key, int edgeLabel) {
        SmartGraphEdgeBase<E, V> edgeBase = edgeNodes.get(key);
        if (edgeBase != null) {
            activeAll();
            edgeBase.setCost(edgeLabel);
            return true;
        }
        return false;
    }


    /**
     * Renames a node and updates its connections.
     *
     * @param v1 the current label of the node
     * @param v2 the new label for the node
     * @return false as the current implementation does not indicate success
     * @throws IllegalArgumentException if the node does not exist
     *
     * @author vittoriopiotti
     */
    public boolean renameNode(char v1,char v2){
        try{
            activeAll();
            List<EdgeDTO> edges = new ArrayList<>();
            if(isExistNode(v1)) {
                for (Edge<E, V> e : edgeNodes.keySet()) {
                    if (isConnectedNode(edgeNodes.get(e), getNodeBase(v1))) {
                        char vi = edgeNodes.get(e).getInbound().getAttachedLabel().getText().charAt(0);
                        char vo = edgeNodes.get(e).getOutbound().getAttachedLabel().getText().charAt(0);
                        edges.add(new EdgeDTO(
                                v1 == vi ? v2 : vi,
                                v1 == vo ? v2 : vo,
                                edgeNodes.get(e).getCost(),
                                isDoubleEdge(e) ? edgeNodes.get(e).getDirection() : getDirectionRotated(edgeNodes.get(e).getDirection())
                        ));
                    }
                }
                deleteNode(v1);
                newNode(v2);
                if(!edges.isEmpty()){
                    for (EdgeDTO edge : edges) {
                        newEdge(edge.getFrom(),edge.getTo(),edge.getCost(),edge.getIsArrowed());
                    }
                }
            }
        }catch(Exception ignored){

        }
        return false;
    };





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
     * Creates a new node at specified coordinates with a given label.
     *
     * @param x the x-coordinate of the new node
     * @param y the y-coordinate of the new node
     * @param v the label of the new node
     * @return true if the node was created, false otherwise
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    public boolean newNode(double x, double y,char v){
        if(v != '\0' && !isExistNode(v)) {
            activeAll();
            Vertex<V> vertex = getModel().insertVertex((V) String.valueOf(v));
            SmartGraphVertexNode<V> vertexNode = new SmartGraphVertexNode<>(vertex, x, y, 0, "circle", true, onClickNode, hideContextMenu);
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
    public boolean deleteNode(Vertex<V> vertexToRemove) {
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
     * Splits a specified edge into two edges.
     *
     * @param edge the edge to be split
     * @return true if the edge was split, false otherwise
     *
     *  @author vittoriopiotti
     */
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
                Edge <E,V> e1 = getModel().insertEdge( outbound, inbound,  (E)generateIdEdge(),cost, AppConstants.DIRECTION_FIRST);
                getModel().insertEdge( inbound, outbound,  (E)generateIdEdge(),cost, AppConstants.DIRECTION_FIRST);
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
     * Rotates the direction of a specified edge.
     *
     * @param edgeNode the edge to be rotated
     * @param direction the new direction of the edge
     * @param isReverse true if the direction is to be reversed, false otherwise
     * @return the new edge created after rotation
     *
     * @author vittoriopiotti
     */
    private Edge<E,V> rotateEdge(SmartGraphEdgeBase<E, V> edgeNode, int direction, boolean isReverse){
        Edge<E,V> edge = edgeNode.getUnderlyingEdge();
        int edgeLabelText = edgeNode.getCost();
        Vertex<V> inbound = edgeNode.getInbound().getUnderlyingVertex();
        Vertex<V> outbound = edgeNode.getOutbound().getUnderlyingVertex();
        //this.getChildren().remove(edgeNode.getAttachedBackground()); FORSE SENNO DAVA ERRORE MA NON PENSO
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
    public boolean  rotateEdge( Edge<E, V>edge){
        if(isExistEdge(edge)) {
            activeAll();
            SmartGraphEdgeBase<E, V> edgeNode = edgeNodes.get(edge);
            if (edgeNode instanceof SmartGraphEdgeLine<E, V>) {
                final int NEW_DIRECTION = edgeNode.getDirection() == AppConstants.DIRECTION_FIRST ?
                        AppConstants.DIRECTION_SECOND
                        : edgeNode.getDirection() == AppConstants.DIRECTION_SECOND ?
                        AppConstants.BIDIRECTIONAL :
                        AppConstants.DIRECTION_FIRST;
                rotateEdge(edgeNode, NEW_DIRECTION, edgeNode.getDirection() != AppConstants.BIDIRECTIONAL);
                updateViewModel();
            } else {
                final int NEW_DIRECTION_FIRST = edgeNode.getDirection() == AppConstants.DIRECTION_FIRST ?
                        AppConstants.DIRECTION_SECOND :
                        AppConstants.DIRECTION_FIRST;
                rotateEdge(edgeNode, NEW_DIRECTION_FIRST, false);
                rotateEdge(getOppositeEdge(edgeNode), NEW_DIRECTION_FIRST, false);
                updateViewModel();
            }
            return true;
        }

        return false;
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
            if (isDoubleEdge(e) && direction == AppConstants.BIDIRECTIONAL) {
                return false;
            }
            if (direction != edgeNodes.get(e).getDirection()) {
                switch (edgeNodes.get(e).getDirection()) {
                    case AppConstants.DIRECTION_FIRST:
                        if (direction == AppConstants.DIRECTION_SECOND) {
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        }
                        return true;
                    case AppConstants.DIRECTION_SECOND:
                        if (direction == AppConstants.DIRECTION_FIRST) {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                        }
                        return true;
                    case AppConstants.BIDIRECTIONAL:
                        if (direction == AppConstants.DIRECTION_FIRST) {
                            rotateEdge(v1, v2);
                        } else {
                            rotateEdge(v1, v2);
                            rotateEdge(v1, v2);
                        }
                        return true;
                    default:
                        return false;
                }
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
            return true;
        }catch(Exception ignored){
        }
        return false;
    }



    /**
     * Deletes the specified edge from the graph.
     *
     * @param edge the edge to be deleted
     * @return true if the edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteEdge (Edge<E, V> edge){
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
                    getModel().insertEdge(direction == AppConstants.DIRECTION_FIRST ? inbound : outbound, direction == AppConstants.DIRECTION_FIRST ? outbound : inbound, (E) generateIdEdge(), cost, direction);
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
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @return true if the edge was successfully deleted, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean deleteEdge(char v1, char v2){
        try {
            SmartGraphEdgeBase<E,V> e = edgeNodes.get(getSpecificEdge(v1,v2));
            if(isDoubleEdge(e.getUnderlyingEdge())) {
                if (e.getDirection() == AppConstants.DIRECTION_FIRST) {
                    e = getOppositeEdge(e);
                }
            }
            return deleteEdge(e.getUnderlyingEdge());
        }catch (Exception ignored) {
        }
        return false;
    }

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
        return newEdge(direction == AppConstants.DIRECTION_SECOND ? v2 : v1, direction == AppConstants.DIRECTION_SECOND ? v1 : v2, cost, direction != AppConstants.BIDIRECTIONAL) && rotateEdge(v1, v2, direction);
    }

    /**
     * Creates a new edge between two nodes specified by their labels with the given cost.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     *  @author vittoriopiotti
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
     * Creates a new edge between two specified vertices with the given cost and direction.
     *
     * @param v1 the first vertex
     * @param v2 the second vertex
     * @param cost the cost of the edge
     * @param direction the direction of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    @SuppressWarnings("unchecked")
    public boolean newEdge(Vertex<V> v1, Vertex<V> v2,int cost,int direction) {
        try {
            if (
                    isExistNode(v1) &&              //exist inbound node
                            isExistNode(v2) &&              //exist outbound node
                            cost > 0 &&                     //label number > 0
                            isActiveVertex(v1) &&           //node not selected (active)
                            !v1.equals(v2)                  //different nodes to no loop
            ) {

                activeAll();
                this.getModel().insertEdge(v2, v1, (E) generateIdEdge(), cost, direction);
                updateViewModel();
                activeAllNodes();
                return true;    //edge added
            }
        }catch(Exception ignored){

        }
        return false;       //edge not added
    }

    /**
     * Sets the cost of the edge between two nodes specified by their labels.
     *
     * @param v1 the label of the first node
     * @param v2 the label of the second node
     * @param cost the new cost to set for the edge
     * @return true if the cost was successfully set, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean setCost(char v1, char v2, int cost){
        try{
            Edge<E,V> e = getSpecificEdge(v1,v2);

            renameEdge(e,cost);
            return true;
        }catch(Exception ignored){

        }
        return false;
    };

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
                    return rotateEdge(v1, v2,AppConstants.BIDIRECTIONAL);
                }else{
                    if(edgeNodes.get(e).getDirection() == AppConstants.BIDIRECTIONAL){
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
     * Get edge direction between two nodes.
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer representing the direction of the edge between the two nodes:
     *         {@link AppConstants#BIDIRECTIONAL}, {@link AppConstants#DIRECTION_FIRST},
     *         or {@link AppConstants#DIRECTION_SECOND}.
     *
     * @author vittoriopiotti
     */
    public int getDirection(char v1, char v2) {
        return Objects.requireNonNull(getSpecificEdge(v1, v2)).getDirection();
    }


    /**
     * Gets the rotated direction of the edge between two nodes.
     * <br>
     * This method rotates the direction of the edge like a wheel, returning:
     * <ul>
     *     <li>{@link AppConstants#DIRECTION_SECOND} if the original direction was {@link AppConstants#DIRECTION_FIRST}</li>
     *     <li>{@link AppConstants#BIDIRECTIONAL} if the original direction was {@link AppConstants#DIRECTION_SECOND}</li>
     *     <li>{@link AppConstants#DIRECTION_FIRST} if the original direction was {@link AppConstants#BIDIRECTIONAL}</li>
     * </ul>
     *
     * @param dir the original direction of the edge
     * @return an integer representing the new direction of the edge
     *
     * @author vittoriopiotti
     */
    public int getDirectionRotated(int dir){
        return dir == AppConstants.DIRECTION_FIRST ? AppConstants.DIRECTION_SECOND : dir == AppConstants.DIRECTION_SECOND ? AppConstants.DIRECTION_FIRST : AppConstants.BIDIRECTIONAL;
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
     *         - {@link AppConstants#SUCCESS} if the upload was successful
     *         - {@link AppConstants#ERROR} if an error occurred during upload
     *         - {@link AppConstants#INTERRUPTED} if the file is null
     *
     * @author vittoriopiotti
     */
    public int uploadJSON(File file){
        if(file != null) {
            try {
                GraphDTO dto = new GraphDTO(file);
                setGraph(dto,true);
                return AppConstants.SUCCESS;
            } catch (Exception ignored) {
                return AppConstants.ERROR;
            }
        }

        return AppConstants.INTERRUPTED;
    }


    /**
     * Opens a file chooser dialog to upload a JSON file and set the graph data.
     *
     * @param window the parent window for the file chooser dialog
     * @return a status code indicating the result of the operation:
     *         - {@link AppConstants#SUCCESS} if the upload was successful
     *         - {@link AppConstants#ERROR} if an error occurred during upload
     *         - {@link AppConstants#INTERRUPTED} if no file was selected
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
                return AppConstants.SUCCESS;
            } catch (Exception ignored) {
                return AppConstants.ERROR;
            }
        }

        return AppConstants.INTERRUPTED;
    }



    /**
     * Downloads the current graph data to a JSON file, using a file chooser dialog.
     *
     * @param window the parent window for the file chooser dialog
     * @return a status code indicating the result of the operation:
     *         - {@link AppConstants#SUCCESS} if the download was successful
     *         - {@link AppConstants#ERROR} if an error occurred during download
     *         - {@link AppConstants#INTERRUPTED} if no file was selected
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
     *         - {@link AppConstants#SUCCESS} if the download was successful
     *         - {@link AppConstants#ERROR} if an error occurred during download
     *         - {@link AppConstants#INTERRUPTED} if the file is null
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
     *         - {@link AppConstants#SUCCESS} if the download was successful
     *         - {@link AppConstants#ERROR} if an error occurred during download
     *         - {@link AppConstants#INTERRUPTED} if no file was selected
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
                fileWriter.write(dto.getJson());
                return AppConstants.SUCCESS;
            } catch (IOException ignored) {
                return AppConstants.ERROR;
            }
        } else {
            return AppConstants.INTERRUPTED;
        }
    }

    /**
     * Downloads the given graph data to a specified JSON file.
     *
     * @param file the file to which the graph data will be written
     * @param dto the graph data to be downloaded
     * @return a status code indicating the result of the operation:
     *         - {@link AppConstants#SUCCESS} if the download was successful
     *         - {@link AppConstants#ERROR} if an error occurred during download
     *         - {@link AppConstants#INTERRUPTED} if the file is null
     *
     * @author vittoriopiotti
     */
    public int downloadJSON(File file,GraphDTO dto) {
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(dto.getJson());
                return AppConstants.SUCCESS;
            } catch (IOException ignored) {
                return AppConstants.ERROR;
            }
        } else {
            return AppConstants.INTERRUPTED;
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
    public boolean showPath(List<NodeDTO> lpn) {
        try {
            char n = lpn.get(0).label();
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
                    char _n = lpn.get(i).label();
                    n = lpn.get(i - 1).label();
                    if (isExistNode(n) && isExistNode(_n)) {
                        vn = getNodeBase(n);
                        SmartGraphVertexNode<V> _vn = getNodeBase(_n);
                        for (Edge<E, V> e : edgeNodes.keySet()) {

                                if (isDoubleEdge(e)) {
                                    if (edgeNodes.get(e).getDirection() == AppConstants.DIRECTION_FIRST && isEqualEdge(edgeNodes.get(e), _vn, vn)) {
                                        edgeNodes.get(e).resetStyle();
                                    } else if (edgeNodes.get(e).getDirection() == AppConstants.DIRECTION_SECOND && isEqualEdge(edgeNodes.get(e), _vn, vn)) {
                                        getOppositeEdge(edgeNodes.get(e)).resetStyle();
                                    }
                                } else if (e.getDirection() == AppConstants.BIDIRECTIONAL
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
     *
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
            adjustPosition.run(); // Esegui l'operazione di aggiustamento
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
                future.complete(null); // Completa il futuro
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
                    isDoubleEdge(e) ? i : d == AppConstants.DIRECTION_SECOND ? i : o,
                    isDoubleEdge(e) ? o : d == AppConstants.DIRECTION_SECOND ? o : i,
                    l,
                    d
            ));
        }
        return new GraphDTO(nodes, edges);
    }






    /**
     * Checks if the given edge is a double edge.
     *
     * An edge is considered a double edge if it is not an instance of
     * SmartGraphEdgeLine. This helps differentiate between single and
     * double edges in the graph.
     *
     * @param edge the edge to check
     * @return true if the edge is a double edge; false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isDoubleEdge(Edge<E, V> edge) {
        return ! (edgeNodes.get(edge) instanceof SmartGraphEdgeLine);
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
                    if(isEqualEdge(edgeNodes.get(e),vb2,vb1) && edgeNodes.get(e).getDirection() == AppConstants.DIRECTION_FIRST ) {
                        return e;
                    }else if(isEqualEdge(edgeNodes.get(e),vb1,vb2)) {
                        return e;
                    }
                }else {
                    if(isConnectedEdge(edgeNodes.get(e), vb2, vb1)) {
                        if(edgeNodes.get(e).getDirection() == AppConstants.BIDIRECTIONAL){
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
                    if (e.getDirection() == AppConstants.BIDIRECTIONAL) {
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
        return isEqualEdge(e,v2,v1) || isEqualEdge(e,v1,v2);

    };


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
        return e.getOutbound().equals(v1) || e.getInbound().equals(v1) ;

    };


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
                isArrowed ? AppConstants.DIRECTION_FIRST  :  AppConstants.BIDIRECTIONAL     //edge direction: DIRECTION_FIRST, DIRECTION_SECOND, BIDIRECTIONAL
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
     * Checks if a specified vertex node exists in the graph.
     *
     * @param v the vertex to check for existence
     * @return true if the node exists, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistNode(Vertex<V> v){
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
    public boolean isExistEdge(Edge<E,V> e){
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
    public boolean isExistEdge(Vertex<V> v2, Vertex<V> v1){
        //search edge
        for (Edge<E,V> e : edgeNodes.keySet()) {
            if(
                    edgeNodes.get(e).getInbound() == vertexNodes.get(v1) &&                        //edge with inbound node
                            edgeNodes.get(e).getOutbound() == vertexNodes.get(v2) &&               //edge with outbound node
                            edgeNodes.get(e).getDirection() != AppConstants.BIDIRECTIONAL    //edge not bidirectional
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
     * Checks if an edge exists between two specified vertex characters.
     *
     * @param v2 the outbound vertex character
     * @param v1 the inbound vertex character
     * @return true if the edge exists, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistEdge(char v2, char v1){
        //search edge
        if(isExistNode(v1) && isExistNode(v2)) {
            Vertex<V> vv1 = null;
            Vertex<V> vv2 = null;
            for (Vertex<V> v : vertexNodes.keySet()) {
                SmartGraphVertexNode<V> app = vertexNodes.get(v);
                if (app.getAttachedLabel().getText().charAt(0) == v1) {
                    vv1 = app.getUnderlyingVertex();
                }
                if (app.getAttachedLabel().getText().charAt(0) == v2 ) {
                    vv2 = app.getUnderlyingVertex();
                }
            }
            try {
                for (Edge<E, V> e : edgeNodes.keySet()) {
                    if (
                            edgeNodes.get(e).getInbound() == vertexNodes.get(vv1) &&                        //edge with inbound node
                                    edgeNodes.get(e).getOutbound() == vertexNodes.get(vv2) &&               //edge with outbound node
                                    edgeNodes.get(e).getDirection() != AppConstants.BIDIRECTIONAL     //edge not bidirectional
                    ) {
                        return true;        //exist edge
                    }
                }
            } catch (Exception ignored) {
                return false;               //edge not exist cause node not found
            }
        }
        return false;                   //edge not found
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
            newNode( node.label());
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

                adjustPosition.run();
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
    public String generateIdEdge() {
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
    public boolean isActiveVertex(Vertex<V> v){

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
    public boolean disabledConnectionsWithNode(Vertex<V> param){
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
    };


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
        background.setOnMouseClicked(event -> {
            onClickArrow.accept(event, edgeNode.getUnderlyingEdge());
        });
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
            onClickBackground.accept(event);
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
     * @throws Exception if an error occurs while handling the event
     *
     * @author vittoriopiotti
     */
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
     * @throws Exception if an error occurs while handling the event
     * @author vittoriopiotti
     */
    private void handleMouseExited(MouseEvent event, SmartGraphEdgeBase<String, String> graphEdge, SmartLabel label, Edge<E, V> edge) {
        // Revert to default style or perform other actions
        graphEdge.applyDefaultStyle();

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
    public void removeNode(Vertex<V> vertexToRemove) {
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
        return new SmartGraphVertexNode<>(v, x, y, shapeRadius, shapeType, graphProperties.getVertexAllowUserMove(),onClickNode,hideContextMenu);
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
    public void addVertex(SmartGraphVertexNode<V> v) {
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
    @SuppressWarnings("all")
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
            arrow.setOnMouseClicked(event -> {
                onClickArrow.accept(event, graphEdge.getUnderlyingEdge());
            });
            graphEdge.setOnMouseClicked_(event -> {
                onClickArrow.accept(event, graphEdge.getUnderlyingEdge());
            });
            if(graphEdge.getDirection() == AppConstants.BIDIRECTIONAL){
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
    public void removeEdge(SmartGraphEdgeBase<E, V> e) {
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
            if(edgeNode.getDirection() == AppConstants.BIDIRECTIONAL){
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
    public BooleanProperty automaticLayoutProperty() {
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
    public void setAutomaticLayoutStrategy(ForceDirectedLayoutStrategy<V> strategy) {
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
    public Graph<V, E> getModel() {
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
    protected final Collection<SmartGraphEdge<E, V>> getSmartEdges() {
        return new ArrayList<>(this.edgeNodes.values());
    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph, immediately returning to the caller.
     * <br/>
     * This method invokes the refresh in the graphical
     * thread through Platform.runLater(), so its not guaranteed that the visualization is in sync
     * immediately after this method finishes. That is, this method
     * immediately returns to the caller without waiting for the update to the
     * visualization.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     *
     * @author brunomnsilva
     */
    public void update() {
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
    public void updateAndWait() {
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
    public void setVertexLabelProvider(SmartLabelProvider<V> labelProvider) {
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
    public void setEdgeLabelProvider(SmartLabelProvider<E> labelProvider) {
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
    public void setVertexRadiusProvider(SmartRadiusProvider<V> vertexRadiusProvider) {
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
    public void setVertexShapeTypeProvider(SmartShapeTypeProvider<V> vertexShapeTypeProvider) {
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
    public SmartStylableNode getStylableEdge(Edge<E, V> edge) {
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
    public SmartStylableNode getStylableEdge(E edgeElement) {
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
    public SmartStylableNode getStylableLabel(Vertex<V> v) {
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
    public SmartStylableNode getStylableLabel(Edge<E,V> e) {
        SmartGraphEdgeBase<E,V> edge = edgeNodes.get(e);

        return edge != null ? edge.getStylableLabel() : null;
    }


    /**
     * Loads the stylesheet and applies the .graph class to this panel.
     *
     * @author brunomnsilva
     */
    private void loadAndApplyStylesheet(URI cssFile) {

        String css = Objects.requireNonNull(getClass().getResource("/smartgraph.css")).toExternalForm();
        if (css != null) {
            this.getStylesheets().add(css);
        }
    }


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

