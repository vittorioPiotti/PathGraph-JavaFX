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



package com.vittoriopiotti.pathgraph.containers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.vittoriopiotti.pathgraph.dto.GraphDTO;
import com.vittoriopiotti.pathgraph.dto.NodeDTO;
import com.vittoriopiotti.pathgraph.graph.*;
import com.vittoriopiotti.pathgraph.graphview.*;
import com.vittoriopiotti.pathgraph.constants.AppConstants;






/**
 * The PathGraph class represents a graphical panel that extends {@link Pane}
 * and manages the logic for handling a graph's display and interaction.
 * It ensures that the underlying graph functionalities are scalable,
 * providing all the necessary features to create and manipulate graphs
 * effectively. This class is designed to be used as a standalone component
 * for graph management without any user interface restrictions.
 *
 * @param <V> The type of the graph's nodes.
 * @param <E> The type of the graph's edges.
 *
 * @author vittoripiotti
 */

public class PathGraph<V,E> extends Pane {


    /**
     * Initial X position for dragging.
     */
    private double initialX;

    /**
     * Initial Y position for dragging.
     */
    private double initialY;

    /**
     * Zoom factor used for scaling the graph.
     */
    final double ZOOM_FACTOR = 1.05;

    /**
     * Maximum allowed zoom level.
     */
    final double MAX_ZOOM = 0.2;

    /**
     * Minimum allowed zoom level.
     */
    final double MIN_ZOOM = 5.0;

    /**
     * The main container for the graph. Uses a {@link StackPane} for managing the display.
     */
    private final StackPane graphContainer = new StackPane();

    /**
     * Defines the clipping bounds for the graph, using a {@link Rectangle}.
     */
    private final Rectangle clipBounds = new Rectangle();

    /**
     * Runnable for closing the context menu of the graph, if present.
     */
    private Runnable closeContextMenu;

    /**
     * Consumer to handle zoom level changes.
     */
    private Consumer<Double> onChangeZoom;

    /**
     * Flag indicating if dragging the graph is active.
     */
    private boolean isDraggedActive = true;

    /**
     * Panel for displaying the graph, using a {@link SmartGraphPanel}.
     */
    private final SmartGraphPanel<V, E> graphView;


    /**
     * Initializes a PathGraph with custom parameters for graph interactions and layout.
     *
     * @param g the graph structure to display in the graph panel.
     * @param closeContextMenu a Runnable to close the context menu when needed.
     * @param onClickArrow a BiConsumer that handles mouse click events on graph edges (arrows).
     * @param onClickNode a BiConsumer that handles mouse click events on graph nodes (vertices).
     * @param onClickBackground a Consumer that handles mouse click events on the background of the graph panel.
     * @param onChangeZoom a Consumer that triggers when the zoom level of the graph changes.
     * @param doAdjustPosition a Runnable to adjust positions of the graph elements after layout changes.
     */
    @SuppressWarnings("all")
    public PathGraph(Graph<String, String> g, Runnable closeContextMenu, BiConsumer<MouseEvent, Edge<E, V>> onClickArrow, BiConsumer<MouseEvent, Vertex<V>> onClickNode, Consumer<MouseEvent> onClickBackground, Consumer<Double> onChangeZoom, Runnable doAdjustPosition) {
        this.onChangeZoom = onChangeZoom;
        this.closeContextMenu = closeContextMenu;

        graphView = (SmartGraphPanel<V,E>)new SmartGraphPanel(
                g,
                new SmartCircularSortedPlacementStrategy(),
                new ForceDirectedSpringGravityLayoutStrategy<>(35.0, 2.0, 20.0, 0.8, 0.01),
                SmartArrow.FILL_ARROW,
                6,
                4,
                Color.WHITESMOKE,
                7.0,
                0.7,
                this.closeContextMenu,
                onClickArrow,
                onClickNode,
                onClickBackground,
                doAdjustPosition
        );

        init();
    }

    /**
     * Initializes a PathGraph with interaction handlers and zoom functionality.
     *
     * @param g the graph structure to display in the graph panel.
     * @param onClickArrow a BiConsumer that handles mouse click events on graph edges (arrows).
     * @param onClickNode a BiConsumer that handles mouse click events on graph nodes (vertices).
     * @param onClickBackground a Consumer that handles mouse click events on the background of the graph panel.
     * @param onChangeZoom a Consumer that triggers when the zoom level of the graph changes.
     */
    public PathGraph(Graph<String, String> g, BiConsumer<MouseEvent, Edge<E, V>> onClickArrow, BiConsumer<MouseEvent, Vertex<V>> onClickNode, Consumer<MouseEvent> onClickBackground, Consumer<Double> onChangeZoom) {
        this(
                g,
                ()->{},
                onClickArrow,
                onClickNode,
                onClickBackground,
                onChangeZoom,
                        ()->{}
        );
    }

    /**
     * Initializes a PathGraph with default interaction handlers and zoom functionality.
     *
     * @param g the graph structure to display in the graph panel.
     */
    public PathGraph(Graph<String, String> g) {
        this(
                g,
                () -> {},
                (event, edge) -> {},
                (event, vertex) -> {},
                event -> {},
                zoom ->{},
                ()->{}
        );
    }


    /**
     * Initializes a default PathGraph with no specific interactions or zoom functionality.
     */
    public PathGraph() {
        this(
                new DigraphEdgeList<>(),
                () -> {},
                (event, edge) -> {},
                (event, vertex) -> {},
                event -> {},
                zoom ->{},
                ()->{}
        );
    }

    /**
     * Checks if the given edge is a double edge in the graph.
     *
     * @param edge the edge to check.
     * @return true if the edge is a double edge, false otherwise.
     */
    public boolean isDoubleEdge(Edge<E, V> edge){
        return graphView.isDoubleEdge(edge);
    }

    /**
     * Gets a label for creating a new node in the graph.
     *
     * @return the label string for a new node.
     */
    public String  getNewNodeLabel() {
        return graphView.getNewNodeLabel();
    }



    /**
     * Activates all nodes in the graph, making them interactive or visible as required.
     */
    public void activeAllNodes(){
        graphView.activeAllNodes();
    }

    /**
     * Activates all edges in the graph, making them interactive or visible as required.
     */
    public void activeAllEdges(){
        graphView.activeAllEdges();
    }

    /**
     * Activates all nodes and edges in the graph, making the entire graph interactive or visible.
     */
    public void activeAll(){
        graphView.activeAll();
    }



    /**
     * Gets the stylable node associated with the specified edge.
     *
     * @param edge the edge for which to get the stylable node.
     * @return the stylable node representing the edge.
     */
    public SmartStylableNode getStylableEdge(
            Edge<E, V> edge) {
        return graphView.getStylableEdge(edge);
    }

    /**
     * Gets the stylable node associated with the specified vertex.
     *
     * @param vertex the vertex for which to get the stylable node.
     * @return the stylable node representing the vertex.
     */
    public SmartStylableNode getStylableVertex(Vertex <V> vertex){
        return graphView.getStylableVertex(vertex);
    }





    /**
     * Disables all connections (edges) associated with the given node (vertex) in the graph.
     *
     * @param param the vertex whose connections are to be disabled.
     * @return true if the connections were successfully disabled, false otherwise.
     */
    public boolean disabledConnectionsWithNode(Vertex<V> param){
        return graphView.disabledConnectionsWithNode(param);
    }

    /**
     * Resets the styles of all graph elements (nodes, edges) to their default states.
     */
    public void resetStyles(){
        graphView.resetStyles();
    }

    /**
     * Sets the graph data for the graph panel using a GraphDTO object.
     *
     * @param pg the GraphDTO object representing the new graph to be displayed.
     */
    public void setGraph(GraphDTO pg){
        graphView.setGraph(pg);
    }

    /**
     * Enables or disables the listeners for graph interaction.
     *
     * @param state true to enable listeners, false to disable them.
     */
    @SuppressWarnings("unused")
    public void enableListenersGraph(boolean state){
        graphView.setMouseTransparent(!state);
    }

    /**
     * Enables or disables the mouse event listeners for the graph container pane.
     *
     * @param state true to enable listeners, false to disable them.
     */
    public void enableListenersPane(boolean state){

        graphContainer.setOnMousePressed(state ? this::onMousePressed : event -> {});
        graphContainer.setOnMouseDragged(state ? this::onMouseDragged : event -> {});
        graphContainer.setOnMouseReleased(state ? this::onMouseReleased : event -> {});
        graphContainer.setOnScroll(state ? this::onScroll : event -> {});
    }


    /**
     * Checks if a node with the given label exists in the graph.
     *
     * @param v the label of the node to check.
     * @return true if the node exists, false otherwise.
     */
    public boolean isExistNode(char v){
        return graphView.isExistNode(v);
    }

    /**
     * Checks if an edge exists between two vertices in the graph.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @return true if the edge exists, false otherwise.
     */
    public boolean isExistEdge(char v1,char v2){
        return graphView.isExistEdge(v1,v2);
    }

    /**
     * Checks if the edge between two vertices is eligible to be renamed.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @return true if the edge can be renamed, false otherwise.
     */
    public boolean isEdgeToRename(char v1,char v2){
        return graphView.isEdgeToRename(v1,v2);
    }

    /**
     * Low level edge split
     * <br>
     * Splits an edge into two separate edges at a low level.
     *
     * @param edge the edge to be split.
     * @return true if the edge was successfully split, false otherwise.
     */
    public boolean splitEdge (Edge<E, V> edge){
        return graphView.splitEdge(edge);
    }

    /**
     * High level edge split
     * <br>
     * Splits the edge between two vertices into two separate edges.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @return true if the edge was successfully split, false otherwise.
     */
    public boolean splitEdge (char v1,char v2){
        return graphView.splitEdge(v1,v2);
    }




    /**
     * Low level edge deletion
     * <br>
     * Deletes an edge from the graph at a low level, without additional validation.
     *
     * @param edge the edge to be deleted.
     * @return true if the edge was successfully deleted, false otherwise.
     */
    public boolean deleteEdge (Edge<E, V> edge){
        return graphView.deleteEdge(edge);
    }


    /**
     * High level edge deletion
     * <br>
     * Deletes the edge between two vertices from the graph.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @return true if the edge was successfully deleted, false otherwise.
     */
    public boolean deleteEdge (char v1, char v2){
        return graphView.deleteEdge(v1,v2);
    }




    /**
     * Low level node deletion
     * <br>
     * Deletes a node from the graph at a low level without additional checks or UI updates.
     *
     * @param vertexToRemove the vertex to remove from the graph.
     * @return true if the vertex was successfully removed, false otherwise.
     */
    public boolean deleteNode(Vertex<V> vertexToRemove) {
        return graphView.deleteNode(vertexToRemove);
    }

    /**
     * High level node deletion
     * <br>
     * Deletes the node with the specified label from the graph.
     *
     * @param v the label of the node to delete.
     * @return true if the node was successfully deleted, false otherwise.
     */
    public boolean deleteNode (char v){
        return graphView.deleteNode(v);
    }




    /**
     * Low level edge rotation
     * <br>
     * Retrieves the direction of the specified edge at a low level.
     *
     * @param edge the edge whose direction is to be determined.
     * @return true if the edge has a direction, false if it is undirected.
     */
    public boolean rotateEdge( Edge<E, V>edge){
        return graphView.rotateEdge(edge);
    }

    /**
     * High level edge rotation
     * <br>
     * Rotates the edge between two vertices in the graph.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @return true if the edge was successfully rotated, false otherwise.
     */
    public boolean rotateEdge(char v1,char v2){
        return graphView.rotateEdge(v1,v2);
    }

    /**
     * High level edge rotation
     * <br>
     * Rotates the edge between two vertices in the graph with the specified direction.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @param direction the direction to rotate the edge (0 for undirected, 1 for directed).
     * @return true if the edge was successfully rotated, false otherwise.
     */
    @SuppressWarnings("all")
    public boolean rotateEdge(char v1,char v2,int direction){
        return graphView.rotateEdge(v1,v2,direction);
    }

    /**
     * Sets whether the edge between two vertices should be arrowed (directed) or not.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @param isArrowed true if the edge should be arrowed, false otherwise.
     * @return true if the arrow status was successfully updated, false otherwise.
     */
    @SuppressWarnings("all")
    public boolean setArrow(char v1,char v2,boolean isArrowed){
        return graphView.setArrow(v1,v2, isArrowed);
    }

    /**
     * Renames an edge at a low level with the specified new cost value.
     *
     * @param edge the edge to rename.
     * @param cost the new cost value for the edge.
     * @return true if the edge was successfully renamed, false otherwise.
     */
    public boolean renameEdge(Edge<E, V> edge,int cost){
        return graphView.renameEdge(edge,cost);
    }

    /**
     * Renames a node in the graph by changing its label from one character to another.
     *
     * @param v1 the current label of the node.
     * @param v2 the new label to assign to the node.
     * @return true if the node was successfully renamed, false otherwise.
     */
    public boolean renameNode(char v1,char v2){
        return graphView.renameNode(v1,v2);
    }



     /**
     * Get edge direction between two nodes.
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer representing the direction of the edge between the two nodes:
     *         {@link AppConstants#BIDIRECTIONAL}, {@link AppConstants#DIRECTION_FIRST},
     *         or {@link AppConstants#DIRECTION_SECOND}.
     */
    public int getDirection(char v1, char v2) {
        return graphView.getDirection(v1, v2);
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
     */
    public int getDirectionRotated(int dir){
        return  graphView.getDirectionRotated(dir);
    }


    /**
     * Creates a new node at the specified coordinates with the given label.
     *
     * @param x the x-coordinate for the new node.
     * @param y the y-coordinate for the new node.
     * @param v the label of the new node.
     * @return true if the node was successfully created, false otherwise.
     */
    @SuppressWarnings("all")
    public boolean newNode(double x, double y,char v){
        return graphView.newNode(x,y,v);
    }

    /**
     * Creates a new node with the given label.
     *
     * @param v the label of the new node.
     * @return true if the node was successfully created, false otherwise.
     */
    @SuppressWarnings("all")
    public boolean newNode(char v){
        return graphView.newNode(v);
    }

    /**
     * Low level edge creation
     * <br>
     * Creates an edge between two vertices at a low level with the specified direction and label.
     *
     * @param inboundVertex the starting vertex of the edge.
     * @param outboundVertex the ending vertex of the edge.
     * @param edgeLabelText the label text for the edge.
     * @param direction the direction of the edge (0 for undirected, 1 for directed).
     * @return true if the edge was successfully created, false otherwise.
     */
    public boolean newEdge(Vertex<V> inboundVertex, Vertex<V> outboundVertex,int edgeLabelText,int direction){
        return graphView.newEdge(inboundVertex,outboundVertex,edgeLabelText,direction);
    }

    /**
     * High level edge creation
     * <br>
     * Creates a new edge between two nodes with a specified cost.
     *
     * @param v1 the label of the first node.
     * @param v2 the label of the second node.
     * @param cost the cost associated with the edge.
     * @return true if the edge was successfully created, false otherwise.
     */
    public boolean newEdge(char v1, char v2,int cost){
        return graphView.newEdge(v1,v2,cost);
    }

    /**
     * High level edge creation
     * <br>
     * Creates a new edge between two nodes with a specified cost and arrow direction.
     *
     * @param v1 the label of the first node.
     * @param v2 the label of the second node.
     * @param cost the cost associated with the edge.
     * @param isArrowed true if the edge should have an arrow, false if it is undirected.
     * @return true if the edge was successfully created, false otherwise.
     */
    public boolean newEdge(char v1, char v2,int cost,boolean isArrowed){
        return graphView.newEdge(v1,v2,cost,isArrowed);
    }

    /**
     * High level edge creation
     * <br>
     * Creates a new edge between two nodes with a specified cost and direction.
     *
     * @param v1 the label of the first node.
     * @param v2 the label of the second node.
     * @param cost the cost associated with the edge.
     * @param direction the direction of the edge (0 for undirected, 1 for directed).
     * @return true if the edge was successfully created, false otherwise.
     */
    public boolean newEdge(char v1, char v2,int cost,int direction){
        return graphView.newEdge(v1,v2,cost,direction);
    }


    /**
     * Sets the cost of the edge between two vertices in the graph.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @param cost the cost to assign to the edge between the two vertices.
     * @return true if the cost was successfully set, false otherwise.
     */
    public boolean setCost(char v1,char v2, int cost){
        return graphView.setCost(v1,v2,cost);
    }



    /**
     * Checks if the specified vertex is currently active.
     *
     * @param v the vertex to check.
     * @return true if the vertex is active, false otherwise.
     */
    public boolean isActiveVertex(
            Vertex<V> v){return graphView.isActiveVertex(v);
    }


    /**
     * Sets all callback functions for the graph, including mouse event handlers and layout adjustments.
     *
     * @param closeContextMenu the callback to close the context menu.
     * @param onClickArrow the callback for clicking on an arrow (edge).
     * @param onClickNode the callback for clicking on a node.
     * @param onClickBackground the callback for clicking on the background.
     * @param onChangeZoom the callback for zoom changes.
     * @param doAdjustPosition the callback for adjusting node positions.
     */
    public void setAllCallbacks(Runnable closeContextMenu,BiConsumer<MouseEvent, Edge<E, V>> onClickArrow,BiConsumer<MouseEvent, Vertex<V>> onClickNode,Consumer<MouseEvent> onClickBackground, Consumer<Double> onChangeZoom,Runnable doAdjustPosition){
        this.closeContextMenu = closeContextMenu;
        this.onChangeZoom = onChangeZoom;
        graphView.setAllCallbacks(onClickArrow,onClickNode,onClickBackground,doAdjustPosition);
        updateLayout();
        layout();
        graphView.updateViewModel();
    }

    /**
     * Initializes and sets up the graph asynchronously, running it on the JavaFX thread.
     *
     * @return a CompletableFuture representing the asynchronous initialization process.
     */
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(() -> {
            // Questo codice viene eseguito in modo asincrono
            graphView.init(); // Inizializzazione del graphView

            graphContainer.setOnMousePressed(this::onMousePressed);
            graphContainer.setOnMouseDragged(this::onMouseDragged);
            graphContainer.setOnMouseReleased(this::onMouseReleased);
            graphContainer.setOnScroll(this::onScroll);

            updateLayout(); // Aggiornamento del layout
        }, Platform::runLater); // Assicura che venga eseguito nel thread JavaFX
    }


    /**
     * Downloads the graph data as a JSON file, allowing the user to select the destination using a window prompt.
     *
     * @param window the window used for file selection.
     * @return an integer representing the status of the download (e.g., success or failure).
     */
    public int downloadJSON(Window window){
        return graphView.downloadJSON(window);
    }

    /**
     * Downloads the graph data as a JSON file, saving it to the specified file.
     *
     * @param file the file where the JSON data will be saved.
     * @return an integer representing the status of the download (e.g., success or failure).
     */
    public int downloadJSON(File file){
        return graphView.downloadJSON(file);
    }

    /**
     * Clears the entire graph asynchronously.
     *
     * @return a CompletableFuture that will complete when the graph has been cleared
     */
    @SuppressWarnings("all")
    public CompletableFuture<Void> clearGraph(){
        return graphView.clearGraph();
    }


    /**
     * Uploads a JSON representation of the graph using a provided window for file selection.
     *
     * @param window the window to use for the file chooser dialog
     * @return an integer representing the result of the upload operation; can be one of the following:
     *         <ul>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#SUCCESS} if the upload is successful</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#ERROR} if an error occurs during the upload</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    public int uploadJSON(Window window){
        return graphView.uploadJSON(window);
    }

    /**
     * Uploads a JSON representation of the graph from a specified file.
     *
     * @param file the file from which to upload the JSON representation of the graph
     * @return an integer representing the result of the upload operation; can be one of the following:
     *         <ul>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#SUCCESS} if the upload is successful</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#ERROR} if an error occurs during the upload</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    public int uploadJSON(File file){
        return graphView.uploadJSON(file);
    }

    /**
     * Takes a screenshot of the graph, with an option to capture an animated screenshot.
     *
     * @param isAnimated true if the screenshot should be animated; false otherwise
     * @return a CompletableFuture that will complete with the screenshot result; can be one of the following:
     *         <ul>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#SUCCESS} if the screenshot is captured successfully</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#ERROR} if an error occurs during the screenshot process</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    @SuppressWarnings("unused")
    public CompletableFuture<Integer> takeScreenshot(boolean isAnimated) {
        return graphView.takeScreenshot(isAnimated);
    }

    /**
     * Takes a screenshot of the graph.
     *
     * @return a CompletableFuture that will complete with the screenshot result; can be one of the following:
     *         <ul>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#SUCCESS} if the screenshot is captured successfully</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#ERROR} if an error occurs during the screenshot process</li>
     *         <li>{@link com.vittoriopiotti.pathgraph.constants.AppConstants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    @SuppressWarnings("all")
    public CompletableFuture<Integer> takeScreenshot() {
        return graphView.takeScreenshot();
    }

    /**
     * Displays a path represented by a list of NodeDTO objects in the graph.
     *
     * @param lpn a list of NodeDTO objects representing the path to show
     * @return true if the path was successfully displayed; false otherwise
     */
    public boolean showPath(List<NodeDTO> lpn){
        return graphView.showPath(lpn);
    }

    /**
     * Retrieves the current graph data as a GraphDTO object.
     *
     * @return a GraphDTO representing the current state of the graph
     */
    public GraphDTO getGraph(){
        return graphView.getGraph();
    }

    /**
     * Handles mouse release events by resetting the cursor to the default state.
     *
     * @param event the MouseEvent representing the mouse release action
     */
    private void onMouseReleased(MouseEvent event){graphContainer.setCursor(Cursor.DEFAULT);}

    /**
     * Initializes the graph view and its layout properties.
     */
    private void init(){
        graphView.setAutomaticLayout(true);
        graphContainer.getChildren().add(graphView);
        StackPane.setAlignment(graphView, Pos.CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), null, null)));
        this.getChildren().add(graphContainer);
        graphContainer.setPrefSize(2000, 2000);
        graphContainer.setMaxSize(2000, 2000);
        graphContainer.setMinSize(2000, 2000);
        clipBounds.setWidth(0);
        clipBounds.setHeight(0);
        graphContainer.setClip(clipBounds);
        widthProperty().addListener((obs, oldVal, newVal) -> updateLayout());
        heightProperty().addListener((obs, oldVal, newVal) -> updateLayout());
    }

    /**
     * Updates the layout of the graph view based on the current dimensions of the container.
     */
    private void updateLayout() {
        clipBounds.setWidth(getWidth());
        clipBounds.setHeight(getHeight());
        graphContainer.setClip(clipBounds);
        double translateX = (getWidth() - graphView.getWidth()) / 2;
        double translateY = (getHeight() - graphView.getHeight()) / 2;
        graphView.setTranslateX(translateX);
        graphView.setTranslateY(translateY);
    }

    /**
     * Handles mouse press events to initiate dragging of the graph view.
     *
     * @param event the MouseEvent representing the mouse press action
     */
    private void onMousePressed(MouseEvent event){
        if (isDraggedActive) {
            initialX = event.getSceneX() - graphView.getTranslateX();
            initialY = event.getSceneY() - graphView.getTranslateY();
            closeContextMenu.run();
        }
    }

    /**
     * Handles mouse drag events to move the graph view within its container.
     *
     * @param event the MouseEvent representing the mouse drag action
     */
    @SuppressWarnings("all")
    private void onMouseDragged(MouseEvent event){
        if ( isDraggedActive) {
            graphContainer.setCursor(Cursor.CLOSED_HAND);
            double deltaX = event.getSceneX() - initialX;
            double deltaY = event.getSceneY() - initialY;
            Bounds graphViewBounds = graphView.getBoundsInParent();
            Bounds containerBounds = graphContainer.getBoundsInLocal();
            boolean isCompletelyOutside =
                    (graphViewBounds.getMaxX() < containerBounds.getMinX() + 200) ||
                            (graphViewBounds.getMinX() > containerBounds.getMaxX() - 200) ||
                            (graphViewBounds.getMaxY() < containerBounds.getMinY() + 200) ||
                            (graphViewBounds.getMinY() > containerBounds.getMaxY() - 200);
            if (isCompletelyOutside) {
                adjustPosition(0.7);
            } else {
                graphView.setTranslateX(deltaX);
                graphView.setTranslateY(deltaY);
            }
            closeContextMenu.run();
        }
        event.consume();
    }

    /**
     * Adjusts the position of the graph view based on the specified x and y offsets.
     *
     * @param x the x-offset for adjusting the position
     * @param y the y-offset for adjusting the position
     */
    public void doDrag(double x,double y){
        Bounds graphViewBounds = graphView.getBoundsInParent();
        Bounds containerBounds = graphContainer.getBoundsInLocal();
        boolean isCompletelyOutside =
                (graphViewBounds.getMaxX() < containerBounds.getMinX() + 200) ||
                        (graphViewBounds.getMinX() > containerBounds.getMaxX() - 200) ||
                        (graphViewBounds.getMaxY() < containerBounds.getMinY() + 200) ||
                        (graphViewBounds.getMinY() > containerBounds.getMaxY() - 200);
        if (isCompletelyOutside) {
            adjustPosition(0.7);
        } else {
            if(x != 0) {
                graphView.setTranslateX(graphView.getTranslateX() + x);
            }
            if(y != 0) {
                graphView.setTranslateY(graphView.getTranslateY() + y);
            }

        }

    }

    /**
     * Handles scroll events to zoom in or out of the graph view.
     *
     * @param event the ScrollEvent representing the scroll action
     */
    private void onScroll(ScrollEvent event){
        double zoomFactor = ZOOM_FACTOR;
        if (event.getDeltaY() < 0) {
            zoomFactor = 1 / ZOOM_FACTOR;
        }
        double zoomScale = graphView.getScaleX() * zoomFactor;
        zoomScale = Math.max(zoomScale, MAX_ZOOM);
        zoomScale = Math.min(zoomScale, MIN_ZOOM);
        graphView.setScaleX(zoomScale);
        graphView.setScaleY(zoomScale);
        onChangeZoom.accept(zoomScale);
        closeContextMenu.run();
        event.consume();
    }

    /**
     * Sets whether the graph layout should be dynamic or automatic.
     *
     * @param isDynamic true if the layout should be dynamic; false otherwise
     */
    public void setAutomaticLayout(boolean isDynamic){
        graphView.setAutomaticLayout(isDynamic);
    }


    /**
     * Zooms the graph view to the specified zoom scale.
     *
     * @param zoomScale the new zoom scale to apply to the graph view
     */
    public void doZoom(double zoomScale){
        zoomScale = Math.max(zoomScale, MAX_ZOOM);
        zoomScale = Math.min(zoomScale, MIN_ZOOM);
        graphView.setScaleX(zoomScale);
        graphView.setScaleY(zoomScale);
    }

    /**
     * Retrieves the current zoom factor for the graph view.
     *
     * @return the current zoom factor
     */
    public double getZoomFactor(){
        return 1 / ZOOM_FACTOR;
    }

    /**
     * Adjusts the position of the graph view smoothly over a specified duration.
     *
     * @param s the duration over which to adjust the position
     */
    @SuppressWarnings("all")
    public void adjustPosition(double s) {
        double targetZoomFactor = getZoomFactor();
        double currentZoomFactor = graphView.getScaleX();
        int steps = 100;
        Duration stepDuration = Duration.seconds(s / steps);
        double zoomStepSize = (targetZoomFactor - currentZoomFactor) / steps;
        Timeline zoomTimeline = new Timeline();
        for (int i = 0; i <= steps; i++) {
            final double newZoomFactor = currentZoomFactor + (i * zoomStepSize);
            KeyFrame keyFrame = new KeyFrame(stepDuration.multiply(i), event -> {
                graphView.setScaleX(newZoomFactor);
                graphView.setScaleY(newZoomFactor);
                onChangeZoom.accept(newZoomFactor);
            });
            zoomTimeline.getKeyFrames().add(keyFrame);
        }
        Bounds containerBounds = graphContainer.getBoundsInLocal();
        double scaledWidth = (graphView.getBoundsInLocal().getWidth() * graphView.getScaleX()) / graphView.getScaleX();
        double scaledHeight = (graphView.getBoundsInLocal().getHeight() * graphView.getScaleY()) / graphView.getScaleY();
        double containerCenterX = (containerBounds.getMinX() + containerBounds.getMaxX()) / 2;
        double containerCenterY = (containerBounds.getMinY() + containerBounds.getMaxY()) / 2;
        double targetX = containerCenterX - (scaledWidth / 2);
        double targetY = containerCenterY - (scaledHeight / 2);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(s), graphView);
        translateTransition.setToX(targetX);
        translateTransition.setToY(targetY);
        ParallelTransition parallelTransition = new ParallelTransition(zoomTimeline, translateTransition);
        parallelTransition.play();
        parallelTransition.setOnFinished(event -> {
            isDraggedActive = true;
        });
    }


}


