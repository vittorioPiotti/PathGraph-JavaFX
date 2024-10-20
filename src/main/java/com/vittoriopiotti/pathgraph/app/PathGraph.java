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



package com.vittoriopiotti.pathgraph.app;



import com.vittoriopiotti.pathgraph.graphview.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.vittoriopiotti.pathgraph.dto.*;
import com.vittoriopiotti.pathgraph.callbacks.*;



/**
 * The PathGraph class represents a graphical panel that extends {@link Pane}
 * and manages the logic for handling a graph's display and interaction.
 * It ensures that the underlying graph functionalities are scalable,
 * providing all the necessary features to create and manipulate graphs
 * effectively. This class is designed to be used as a standalone component
 * for graph management without any user interface restrictions.
 *
 *
 * @author vittoripiotti
 */

public class PathGraph extends Pane {


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
    private final double ZOOM_FACTOR = 1.05;

    /**
     * Maximum allowed zoom level.
     */
    private final double MAX_ZOOM = 0.2;

    /**
     * Minimum allowed zoom level.
     */
    private final double MIN_ZOOM = 5.0;

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
    private ContextMenuCallback closeContextMenu;

    /**
     * Consumer to handle zoom level changes.
     */
    private ZoomCallback onChangeZoom;

    /**
     * Flag indicating if dragging the graph is active.
     */
    private boolean isDraggedActive = true;

    /**
     * Panel for displaying the graph, using a {@link SmartGraphPanel}.
     */
    @SuppressWarnings("all")
    private final SmartGraphPanel graphView;


    /**
     * Initializes a PathGraph with custom parameters for graph interactions and layout.
     *
     * @param closeContextMenu a Runnable to close the context menu when needed.
     * @param onClickArrow a BiConsumer that handles mouse click events on graph edges (arrows).
     * @param onClickNode a BiConsumer that handles mouse click events on graph nodes (vertices).
     * @param onClickBackground a Consumer that handles mouse click events on the background of the graph panel.
     * @param onChangeZoom a Consumer that triggers when the zoom level of the graph changes.
     * @param doAdjustPosition a Runnable to adjust positions of the graph elements after layout changes.
     */
    @SuppressWarnings("all")
    public PathGraph(
                     ContextMenuCallback closeContextMenu,
                     EdgeCallback onClickArrow,
                     NodeCallback onClickNode,
                     BackgroundCallback onClickBackground,
                     ZoomCallback onChangeZoom,
                     AdjustPositionCallback doAdjustPosition
    ){
        this.onChangeZoom = onChangeZoom;
        this.closeContextMenu = closeContextMenu;

        graphView = new SmartGraphPanel(
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
     * @param onClickArrow a BiConsumer that handles mouse click events on graph edges (arrows).
     * @param onClickNode a BiConsumer that handles mouse click events on graph nodes (vertices).
     * @param onClickBackground a Consumer that handles mouse click events on the background of the graph panel.
     * @param onChangeZoom a Consumer that triggers when the zoom level of the graph changes.
     */
    public PathGraph(
            EdgeCallback onClickArrow,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            ZoomCallback onChangeZoom
    ) {
        this(
                () -> {},
                onClickArrow,
                onClickNode,
                onClickBackground,
                onChangeZoom,
                () -> {}
        );
    }

    /**
     * Initializes a PathGraph with default interaction handlers and zoom functionality.
     *
     */
    public PathGraph() {
        this(
                () -> {},
                (event, char1, char2) -> {},
                (event, vertex) -> {},
                event -> {},
                zoom ->{},
                ()->{}
        );
    }



    /**
     * Checks if the given edge is a double edge in the graph.
     *
     * @param v1 start node of the edge to check.
     * @param v2 end node of the edge to check.
     * @return true if the edge is a double edge, false otherwise.
     */
    public boolean isDoubleEdge(char v1, char v2){
        return graphView.isDoubleEdge(v1,v2);
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
     */
    public boolean isDoubleEdge(EdgeDTO edge){
        return graphView.isDoubleEdge(edge);
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
     */
    public boolean isDoubleEdge(NodeDTO start, NodeDTO end){
        return graphView.isDoubleEdge(start,end);
    }


    /**
     * Retrieves a label for creating a new node in the graph.
     *
     * @return a character representing the label for a new node.
     */
    public char getNewRandomNodeLabel() {
        return graphView.getNewRandomNodeLabel();
    }

    /**
     * Retrieves a random character label from existing node labels.
     *
     * @return a character from existing node labels, or a null character if none exist.
     */
    public char getExistRandomNodeLabel() {
        return graphView.getExistRandomNodeLabel();
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
     * Disables all connections (edges) associated with the given node (vertex) in the graph.
     *
     * @param param the vertex whose connections are to be disabled.
     * @return true if the connections were successfully disabled, false otherwise.
     */
    public boolean disabledConnectionsWithNode(char param){
        return graphView.disabledConnectionsWithNode(param);
    }

    /**
     * Checks if the connections with the specified node, based on the given node DTO, are disabled.
     *
     * @param node the NodeDTO representing the node to check for disabled connections
     * @return {@code true} if the connections with the specified node are disabled; {@code false} otherwise
     */
    public boolean disabledConnectionsWithNode(NodeDTO node){
        return graphView.disabledConnectionsWithNode(node);
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
     * Checks if a node with the specified label exists in the graph.
     *
     * @param node the {@link NodeDTO} object containing the label of the node to check for existence
     * @return true if the node exists in the graph, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean isExistNode(NodeDTO node){
       return graphView.isExistNode(node);
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
     * Checks if an edge exists between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return true if the edge exists, false otherwise
     */
    public boolean isExistEdge(NodeDTO start, NodeDTO end){
        return graphView.isExistEdge(start, end);

    }


    /**
     * Checks if an edge between the specified nodes exists in the graph.
     *
     * @param edge the {@link EdgeDTO} object containing the details of the edge to check for existence,
     *             including the labels of the starting and ending nodes
     * @return true if the edge exists in the graph, false otherwise
     */
    public boolean isExistEdge(EdgeDTO edge) {
        return graphView.isExistEdge(edge);
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
     * Checks if the edge between the specified starting and ending nodes is eligible for renaming.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return true if the edge can be renamed, false otherwise
     */
    public boolean isEdgeToRename(NodeDTO start, NodeDTO end){
        return graphView.isEdgeToRename(start, end);
    }


    /**
     * Checks if the specified edge, based on the given edge DTO, needs to be renamed.
     *
     * @param edge the EdgeDTO representing the edge to check for renaming
     * @return {@code true} if the edge needs to be renamed; {@code false} otherwise
     */
    public boolean isEdgeToRename(EdgeDTO edge){
        return graphView.isEdgeToRename(edge);
    }


    /**
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
     * Splits the edge between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge to be split
     * @param end   the {@link NodeDTO} representing the ending node of the edge to be split
     * @return true if the edge was successfully split, false otherwise
     */
    public boolean splitEdge(NodeDTO start, NodeDTO end){
        return graphView.splitEdge(start,end);
    }


    /**
     * Splits an edge in the graph based on the specified edge data transfer object (DTO).
     *
     * @param edge the {@link EdgeDTO} containing the information of the edge to be split,
     *             including the starting node and ending node of the edge
     * @return true if the edge was successfully split, false otherwise
     */
    public boolean splitEdge(EdgeDTO edge) {
        return graphView.splitEdge(edge);
    }


    /**
     * Deletes the edge between two vertices from the graph.
     *
     * @param start the label of the first vertex.
     * @param end the label of the second vertex.
     * @return true if the edge was successfully deleted, false otherwise.
     */
    public boolean deleteEdge (char start, char end){
        return graphView.deleteEdge(start,end);
    }


    /**
     * Deletes the edge between two vertices from the graph.
     *
     * @param start the label of the first vertex.
     * @return true if the edge was successfully deleted, false otherwise.
     */
    public boolean deleteEdge (char start){
        return graphView.deleteEdge(start);
    }


    /**
     * Deletes the edge between the specified nodes in the graph.
     *
     * @param edge the {@link EdgeDTO} object containing the details of the edge to be deleted,
     *             including the labels of the starting and ending nodes
     * @return true if the edge was successfully deleted, false otherwise
     */
    public boolean deleteEdge(EdgeDTO edge){
        return graphView.deleteEdge(edge);
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
        return graphView.isConnectedNode(edge,label);
    }



    /**
     * Checks if there is a connection (edge) between two nodes.
     *
     * @param start the label of the starting node
     * @param end the label of the ending node
     * @return true if there is a connection (edge) between the nodes, false otherwise
     */
    public boolean isConnectedEdge(char start, char end){
        return graphView.isConnectedEdge(start,end);

    }


    /**
     * Checks if the specified label is connected to either of the two nodes defined by their labels.
     *
     * @param start the label of the first node
     * @param end the label of the second node
     * @param label the label to check for connectivity
     * @return true if the specified label is connected to either of the two nodes, false otherwise
     */
    @SuppressWarnings("unused")
    private boolean isConnectedNode(char start, char end, char label) {
        return  graphView.isConnectedNode(start,end,label);
    }


    /**
     * Checks if the specified label is connected to the node defined by its label.
     *
     * @param start the label of the node to check for connectivity
     * @return true if the specified label is connected to the node, false otherwise
     */
    @SuppressWarnings("unused")
    private boolean isConnectedNode(char start) {
        return  graphView.isConnectedNode(start);
    }

    /**
     * Retrieves a list of nodes that are connected to any node in the graph.
     *
     * @return a list of {@link NodeDTO} representing the connected nodes
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectedNodes(){
        return  graphView.getConnectedNodes();
    }


    /**
     * Retrieves a list of edges connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected edges are to be retrieved
     * @return a list of EdgeDTO objects representing the edges connected to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<EdgeDTO> getConnectedEdges(NodeDTO node){
        return graphView.getConnectedEdges(node);
    }


    /**
     * Retrieves a list of nodes that can be connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node for which connectable nodes are to be retrieved
     * @return a list of NodeDTO objects representing the nodes that can be connected to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectableNodes(NodeDTO node){
        return graphView.getConnectableNodes(node);
    }

    /**
     * Retrieves the number of edges connected to the node identified by the given label.
     *
     * @param label the label of the node whose connected edges are to be counted
     * @return the number of edges connected to the specified node
     */
    public int getNumConnectedEdges(char label) {
        return graphView.getNumConnectedEdges(label);
    }

    /**
     * Retrieves the number of edges connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected edges are to be counted
     * @return the number of edges connected to the specified node
     */
    public int getNumConnectedEdges(NodeDTO node) {
        return graphView.getNumConnectedEdges(node);
    }


    /**
     * Retrieves a list of nodes that are connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connected nodes are to be retrieved
     * @return a list of {@link NodeDTO} representing the connected nodes to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectedNodes(char label){
        return  graphView.getConnectedNodes(label);

    }



    /**
     * Retrieves a list of nodes connected to the specified node based on the given node DTO.
     *
     * @param node the NodeDTO representing the node whose connected nodes are to be retrieved
     * @return a list of NodeDTO objects representing the nodes connected to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectedNodes(NodeDTO node) {
        return graphView.getConnectedNodes(node);
    }

    /**
     * Retrieves a list of nodes that can be connected to any node in the graph.
     *
     * @return a list of {@link NodeDTO} representing the connectable nodes
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectableNodes(){
        return  graphView.getConnectableNodes();
    }



    /**
     * Checks if the specified node, based on the given node DTO, is connected to any other node.
     *
     * @param node the NodeDTO representing the node to check for connections
     * @return {@code true} if the node is connected to any other node; {@code false} otherwise
     */
    public boolean isConnectedNode(NodeDTO node) {
        return  graphView.isConnectedNode(node);
    }


    /**
     * Retrieves a list of nodes that can be connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connectable nodes are to be retrieved
     * @return a list of {@link NodeDTO} representing the connectable nodes to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getConnectableNodes(char label){
        return  graphView.getConnectableNodes(label);
    }

    /**
     * Retrieves a list of edges that are connected to the specified node defined by its label.
     *
     * @param label the label of the node for which connected edges are to be retrieved
     * @return a list of {@link EdgeDTO} representing the connected edges to the specified node
     */
    @SuppressWarnings("unchecked")
    public List<EdgeDTO> getConnectedEdges(char label){
        return  graphView.getConnectedEdges(label);
    }


    /**
     * Returns the edge between the specified start and end nodes.
     *
     * @param start The start node.
     * @param end   The end node.
     * @return The {@link EdgeDTO} representing the edge, or {@code null} if not found.
     */
    @SuppressWarnings("unused")
    public EdgeDTO getEdge(char start, char end){
        return graphView.getEdge(start,end);
    }




    /**
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
     * Rotates the edge between two vertices in the graph with the specified direction.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @param direction the direction to rotate the edge (0 for undirected, 1 for directed).
     * @return true if the edge was successfully rotated, false otherwise.
     */
    public boolean rotateEdge(char v1,char v2,int direction){
        return graphView.rotateEdge(v1,v2,direction);
    }


    /**
     * Rotates the edge between the two nodes specified in the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @return true if the edge was successfully rotated, false otherwise
     */
    public boolean rotateEdge(EdgeDTO edge){
        return graphView.rotateEdge(edge);
    }

    /**
     * Rotates the edge between the two nodes specified in the given {@link EdgeDTO} to the specified direction.
     *
     * @param edge      the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @param direction the direction in which to rotate the edge
     * @return true if the edge was successfully rotated to the specified direction, false otherwise
     */
    public boolean rotateEdge(EdgeDTO edge, int direction){
        return graphView.rotateEdge(edge, direction);
    }



    /**
     * Sets the arrow status for the edge between the two nodes specified in the given {@link EdgeDTO}.
     *
     * @param edge      the {@link EdgeDTO} object containing the labels of the starting and ending nodes
     * @param isArrowed true if the edge should be arrowed, false otherwise
     * @return true if the arrow status was successfully set, false otherwise
     */
    public boolean setArrow(EdgeDTO edge,boolean isArrowed){
        return graphView.setArrow(edge,isArrowed);
    }



    /**
     * Sets whether the edge between two vertices should be arrowed (directed) or not.
     *
     * @param v1 the label of the first vertex.
     * @param v2 the label of the second vertex.
     * @param isArrowed true if the edge should be arrowed, false otherwise.
     * @return true if the arrow status was successfully updated, false otherwise.
     */
    public boolean setArrow(char v1,char v2,boolean isArrowed){
        return graphView.setArrow(v1,v2, isArrowed);
    }

    /**
     * Renames an edge at a low level with the specified new cost value.
     *
     * @param start start node of the edge to rename.
     * @param end end node of the edge to rename.
     * @param cost the new cost value for the edge.
     * @return true if the edge was successfully renamed, false otherwise.
     */
    public boolean setCost(char start, char end,int cost){
        return graphView.setCost(start,end,cost);
    }


    /**
     * Sets the cost of the edge specified by the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} representing the edge whose cost is to be set
     * @param cost the new cost to be assigned to the edge
     * @return true if the cost was successfully set, false otherwise
     */
    public boolean setCost(EdgeDTO edge, int cost) {
        return graphView.setCost(edge, cost);
    }

     /**
     * Get edge direction between two nodes.
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer representing the direction of the edge between the two nodes:
     *         {@link Constants#BIDIRECTIONAL}, {@link Constants#NATURAL_DIRECTION},
     *         or {@link Constants#OPPOSITE_DIRECTION}.
     */
    public int getDirection(char v1, char v2) {
        return graphView.getDirection(v1, v2);
    }


    /**
     * Retrieves the direction of the edge specified by the given {@link EdgeDTO}.
     *
     * @param edge the {@link EdgeDTO} representing the edge whose direction is to be retrieved
     * @return the direction of the edge as an integer
     */
    public int getDirection(EdgeDTO edge) {
        return graphView.getDirection(edge);
    }


    /**
     * Retrieves the direction of the edge between the specified starting and ending nodes.
     *
     * @param start the {@link NodeDTO} representing the starting node of the edge
     * @param end   the {@link NodeDTO} representing the ending node of the edge
     * @return an integer representing the direction of the edge
     */
    public int getDirection(NodeDTO start, NodeDTO end) {
        return graphView.getDirection(start,end);
    }

    /**
     * Get edge cost.
     *
     * @param v1 start node.
     * @param v2 end node.
     * @return an integer value of the edge cost
     */
    public int getCost(char v1, char v2) {
        return graphView.getCost(v1, v2);
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
        return graphView.getCost(edge);
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
        return graphView.getCost(start,end);
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
     */
    public int getDirectionRotated(int dir){
        return  graphView.getDirectionRotated(dir);
    }



    /**
     * Returns the number of edges in the graph.
     *
     * @return the total number of edges.
     */
    public int getNumEdges(){
        return graphView.getNumEdges();

    }

    /**
     * Returns the number of nodes in the graph.
     *
     * @return the total number of nodes.
     */
    public int getNumNodes(){
        return graphView.getNumNodes();
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
     */
    public boolean isEqualEdge(char start1, char end1, char start2, char end2){
        return graphView.isEqualEdge(start1,end1,start2,end2);
    }



    /**
     * Checks if the edge specified by the given {@link EdgeDTO} is connected in the graph.
     *
     * @param edge the {@link EdgeDTO} representing the edge to check for connectivity
     * @return true if the edge is connected, false otherwise
     */
    public boolean isConnectedEdge(EdgeDTO edge){
        return graphView.isConnectedEdge(edge);
    }


    /**
     * Compares two edges to determine if they are equal.
     *
     * @param edge1 the first {@link EdgeDTO} to compare
     * @param edge2 the second {@link EdgeDTO} to compare
     * @return true if the two edges are equal, false otherwise
     */
    public boolean isEqualEdge(EdgeDTO edge1, EdgeDTO edge2){
        return graphView.isEqualEdge(edge1,edge2);
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
     */
    public char getConnectableNode(char start){
        return graphView.getConnectableNode(start);
    }




    /**
     * Retrieves a connectable node from the graph based on the specified starting {@link NodeDTO}.
     *
     * @param node the {@link NodeDTO} representing the starting node
     * @return the label of the connectable node
     */
    public char getConnectableNode(NodeDTO node){
        return graphView.getConnectableNode(node);
    }




    /* NODE METHOD - new node */
    /**
     * Creates a new node at the specified coordinates with the given label.
     *
     * @param x the x-coordinate for the new node.
     * @param y the y-coordinate for the new node.
     * @param v the label of the new node.
     * @return true if the node was successfully created, false otherwise.
     */
    public boolean newNode(double x, double y,char v){
        return graphView.newNode(x,y,v);
    }
    /**
     * Creates a new node with the given label.
     *
     * @param v the label of the new node.
     * @return true if the node was successfully created, false otherwise.
     */
    public boolean newNode(char v){
        return graphView.newNode(v);
    }
    /**
     * Creates a new node with a random label in the graph.
     *
     * @return {@code true} if the node is created successfully; {@code false} otherwise.
     */
    public boolean newNode() {
        return graphView.newNode();
    }



    /**
     * Creates a new node in the graph based on the specified node data transfer object (DTO).
     *
     * @param node the {@link NodeDTO} containing the information of the node to be created,
     *             including the node's label
     * @return true if the node was successfully created, false otherwise
     */
    public boolean newNode(NodeDTO node ) {
        return graphView.newNode(node);
    }

    /* NODE METHOD - delete node */
    /**
     * Deletes the node with the specified label from the graph.
     *
     * @param v the label of the node to delete.
     * @return true if the node was successfully deleted, false otherwise.
     */
    public boolean deleteNode (char v){
        return graphView.deleteNode(v);
    }

    /**
     * Deletes the specified node from the graph based on the given node DTO.
     *
     * @param node the NodeDTO representing the node to be deleted
     * @return {@code true} if the node was successfully deleted; {@code false} otherwise
     *
     */
    public boolean deleteNode(NodeDTO node){
        return graphView.deleteNode(node);
    }



    /* NODE METHOD - rename node */
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
     * Renames the node specified by the given {@link NodeDTO} to the new label.
     *
     * @param node     the {@link NodeDTO} representing the node to be renamed
     * @param newLabel the new label for the node
     * @return true if the node was successfully renamed, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean renameNode(NodeDTO node, char newLabel){
        return graphView.renameNode(node,newLabel);
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
        return graphView.renameNode(node,newNode);
    }



    /*EDGE METHOD - new edge */

    /**
     * Creates a new edge in the graph based on the specified edge data transfer object (DTO).
     *
     * @param edge the {@link EdgeDTO} containing the information of the edge to be created,
     *             including the starting node, ending node, cost, and arrow indication
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(EdgeDTO edge){
         return graphView.newEdge(edge);
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
        return graphView.newEdge(direction == Constants.OPPOSITE_DIRECTION ? v2 : v1, direction == Constants.OPPOSITE_DIRECTION ? v1 : v2, cost, direction != Constants.BIDIRECTIONAL) && rotateEdge(v1, v2, direction);
    }
    /**
     * Creates a new edge between two nodes specified by their labels with the given cost.
     *
     * @param start the label of the first node
     * @param end the label of the second node
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start, char end,int cost) {
        return graphView.newEdge(start,end,cost,true);
    }
    /**
     * Creates a new edge between two nodes specified by their labels with the given cost and a flag indicating if the edge is arrowed.
     *
     * @param start the label of the first node
     * @param end the label of the second node
     * @param cost the cost of the edge
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     *
     * @author vittoriopiotti
     */
    public boolean newEdge(char start, char end,int cost,boolean isArrowed) {
        return graphView.newEdge(start, end,cost, isArrowed);
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
        return graphView.newEdge(start,cost,direction);
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
        return graphView.newEdge(start,cost,isArrowed);
    }
    /**
     * Creates a new random edge from the start node to a connectable node with the given cost.
     *
     * @param start the label of the starting node
     * @param cost the cost of the edge
     */
    public boolean newEdge(char start, int cost){
        return graphView.newEdge(start,cost);
    }
    /**
     * Creates a new random edge between two available nodes with the given cost and direction.
     *
     * @param cost the cost of the edge
     * @param direction the direction of the edge
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(int cost, int direction){
        return graphView.newEdge(cost,direction);
    }
    /**
     * Creates a new random edge between two available nodes with the given cost and arrow indication.
     *
     * @param cost the cost of the edge
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(int cost, boolean isArrowed){
        return graphView.newEdge(cost,isArrowed);
    }
    /**
     * Creates a new random edge between two available nodes with the given cost.
     *
     * @param cost the cost of the edge
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(int cost){
        return graphView.newEdge(cost);
    }
    /**
     * Creates a new random edge between two available nodes with a specified arrow indication.
     *
     * @param isArrowed true if the edge should have an arrow, false otherwise
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(boolean isArrowed){
        return graphView.newEdge(isArrowed);
    }
    public boolean newEdge(char start){
        return graphView.newEdge(start);
    }
    /**
     * Creates a new random edge between two available nodes with a random cost.
     *
     * @return true if the edge was successfully created, false otherwise
     */
    public boolean newEdge(){
        return  graphView.newEdge();
    }










    /**
     * Checks if the specified vertex is currently active.
     *
     * @param label the vertex to check.
     * @return true if the vertex is active, false otherwise.
     */
    public boolean isActiveNode(char label){return graphView.isActiveNode(label);}


    /**
     * Checks if the specified node is active in the graph.
     *
     * @param node the {@link NodeDTO} object containing the label of the node to check for activity
     * @return true if the node is active in the graph, false otherwise
     */
    public boolean isActiveNode(NodeDTO node){return graphView.isActiveNode(node);}


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
    public void setAllCallbacks(
            ContextMenuCallback closeContextMenu,
            EdgeCallback onClickArrow,
            NodeCallback onClickNode,
            BackgroundCallback onClickBackground,
            ZoomCallback onChangeZoom,
            AdjustPositionCallback doAdjustPosition
    ){
        this.closeContextMenu = closeContextMenu;
        this.onChangeZoom = onChangeZoom;
        graphView.setAllCallbacks(closeContextMenu,onClickArrow,onClickNode,onClickBackground,doAdjustPosition);
        update();
    }

    /**
     * Sets a callback to be executed to close the context menu.
     *
     * @param closeContextMenu a {@link ContextMenuCallback} that defines the logic for closing the context menu.
     */
    public void setContextMenuCallback(ContextMenuCallback closeContextMenu) {
        this.closeContextMenu = closeContextMenu;
        graphView.setContextMenuCallback(closeContextMenu);
        update();
    }

    /**
     * Sets a callback to be executed when the zoom level changes.
     *
     * @param onChangeZoom a {@link ZoomCallback} that accepts a Double representing the new zoom level.
     */
    @SuppressWarnings("unused")
    public void setZoomCallback(ZoomCallback onChangeZoom) {
        this.onChangeZoom = onChangeZoom;
        update();
    }



    /**
     * Sets a callback to be executed when an edge in the graph is clicked.
     *
     * @param onClickEdge a  {@link EdgeCallback} that accepts a MouseEvent and two Characters (the source and target nodes of the clicked edge).
     */
    public void setEdgeCallback(EdgeCallback onClickEdge) {
        graphView.setEdgeCallback(onClickEdge);
        update();
    }

    /**
     * Sets a callback to be executed when a node in the graph is clicked.
     *
     * @param onClickNode a {@link NodeCallback} that accepts a MouseEvent and a Character (the clicked node).
     */
    public void setNodeCallback(NodeCallback onClickNode) {
        graphView.setNodeCallback(onClickNode);
        update();
    }

    /**
     * Sets a callback to be executed when the background of the graph is clicked.
     *
     * @param onClickBackground a {@link BackgroundCallback}  that accepts a MouseEvent (the event triggered when the background is clicked).
     */
    public void setBackgroundCallback(BackgroundCallback onClickBackground) {
         graphView.setBackgroundCallback(onClickBackground);
        update();
    }

    /**
     * Sets a callback to adjust the position of the graph elements.
     *
     * @param adjustPosition a {@link AdjustPositionCallback} that defines the adjustment logic for the graph's position.
     */
    public void setAdjustPositionCallback(AdjustPositionCallback adjustPosition) {
        graphView.setAdjustPositionCallback(adjustPosition);
        update();
    }

    /**
     * Initializes and sets up the graph asynchronously, running it on the JavaFX thread.
     *
     * @return a CompletableFuture representing the asynchronous initialization process.
     */
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(() -> {
            graphView.init();
            graphContainer.setOnMousePressed(this::onMousePressed);
            graphContainer.setOnMouseDragged(this::onMouseDragged);
            graphContainer.setOnMouseReleased(this::onMouseReleased);
            graphContainer.setOnScroll(this::onScroll);
            updateLayout();
        }, Platform::runLater);
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
    @SuppressWarnings("unchecked")
    public CompletableFuture<Void> clearGraph(){
        return graphView.clearGraph();
    }


    /**
     * Uploads a JSON representation of the graph using a provided window for file selection.
     *
     * @param window the window to use for the file chooser dialog
     * @return an integer representing the result of the upload operation; can be one of the following:
     *         <ul>
     *         <li>{@link Constants#SUCCESS} if the upload is successful</li>
     *         <li>{@link Constants#ERROR} if an error occurs during the upload</li>
     *         <li>{@link Constants#INTERRUPTED} if the operation is interrupted</li>
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
     *         <li>{@link Constants#SUCCESS} if the upload is successful</li>
     *         <li>{@link Constants#ERROR} if an error occurs during the upload</li>
     *         <li>{@link Constants#INTERRUPTED} if the operation is interrupted</li>
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
     *         <li>{@link Constants#SUCCESS} if the screenshot is captured successfully</li>
     *         <li>{@link Constants#ERROR} if an error occurs during the screenshot process</li>
     *         <li>{@link Constants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<Integer> takeScreenshot(boolean isAnimated) {
        return graphView.takeScreenshot(isAnimated);
    }

    /**
     * Takes a screenshot of the graph.
     *
     * @return a CompletableFuture that will complete with the screenshot result; can be one of the following:
     *         <ul>
     *         <li>{@link Constants#SUCCESS} if the screenshot is captured successfully</li>
     *         <li>{@link Constants#ERROR} if an error occurs during the screenshot process</li>
     *         <li>{@link Constants#INTERRUPTED} if the operation is interrupted</li>
     *         </ul>
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<Integer> takeScreenshot() {
        return graphView.takeScreenshot();
    }

    /**
     * Displays a path represented by a list of NodeDTO objects in the graph.
     *
     * @param lpn a list of NodeDTO objects representing the path to show
     * @return true if the path was successfully displayed; false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean showPath(List<NodeDTO> lpn){
        return graphView.showPath(lpn);
    }







    /**
     * Retrieves the current graph data as a {@link GraphDTO} object.
     *
     * @return a {@link GraphDTO} representing the current state of the graph.
     *         This object contains all nodes and edges currently present in the graph.
     */
    public GraphDTO getGraph() {
        return graphView.getGraph();
    }

    /**
     * Retrieves the list of nodes in the graph.
     *
     * @return a list of {@link NodeDTO} representing the nodes.
     * These data transfer objects facilitate graph operations.
     */
    @SuppressWarnings("unchecked")
    public List<NodeDTO> getNodes() {
        return graphView.getNodes();
    }



    /**
     * Retrieves the list of edges in the graph.
     *
     * @return a list of {@link EdgeDTO} representing the edges.
     * These data transfer objects facilitate graph operations.
     */
    @SuppressWarnings("unchecked")
    public List<EdgeDTO> getEdges() {
        return graphView.getEdges();
    }

    /**
     * Retrieves the connections between nodes in the graph.
     *
     * @return a map of {@link NodeDTO} to lists of {@link ConnectionDTO}.
     * This structure enables efficient management of graph connections.
     */
    @SuppressWarnings("unchecked")
    public Map<NodeDTO, List<ConnectionDTO>> getConnections() {
        return graphView.getConnections();
    }


    /**
     * Retrieves the graph's current state as a JSON string, including nodes and edges.
     * This representation allows for easy serialization and data interchange.
     *
     * @return a JSON string of the current {@link GraphDTO} object.
     */
    public String getGraphJson() {
        return graphView.getGraphJson();
    }

    /**
     * Retrieves the current state of the nodes as a JSON string.
     *
     * @return a JSON string representing the graph's nodes.
     */
    public String getNodesJson() {
        return graphView.getNodesJson();
    }

    /**
     * Retrieves the current state of the edges as a JSON string.
     *
     * @return a JSON string representing the graph's edges.
     */
    public String getEdgesJson() {
        return graphView.getEdgesJson();
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
        return graphView.getConnectionsJson();
    }



    /**
     * Adjusts the position of the graph view based on the specified x and y offsets.
     *
     * @param x the x-offset for adjusting the position
     * @param y the y-offset for adjusting the position
     */
    public void doDrag(double x,double y){
        if (isCompletelyOutside()) {
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
                onChangeZoom.handle(newZoomFactor);
            });
            zoomTimeline.getKeyFrames().add(keyFrame);
        }
        ParallelTransition parallelTransition = getParallelTransition(s, zoomTimeline);
        parallelTransition.setOnFinished(event -> isDraggedActive = true);
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
        onChangeZoom.handle(zoomScale);
        closeContextMenu.handle();
        event.consume();
    }


    /**
     * Reactivates callbacks after they have been reset, used after setting new callbacks.
     */
    private void update(){
        updateLayout();
        layout();
        graphView.updateViewModel();
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
            closeContextMenu.handle();
        }
    }

    /**
     * Handles mouse drag events to move the graph view within its container.
     *
     * @param event the MouseEvent representing the mouse drag action
     */
    private void onMouseDragged(MouseEvent event){
        if ( isDraggedActive) {
            graphContainer.setCursor(Cursor.CLOSED_HAND);
            double deltaX = event.getSceneX() - initialX;
            double deltaY = event.getSceneY() - initialY;
            if (isCompletelyOutside()) {
                adjustPosition(0.7);
            } else {
                graphView.setTranslateX(deltaX);
                graphView.setTranslateY(deltaY);
            }
            closeContextMenu.handle();
        }
        event.consume();
    }

    /**
     * Checks if the graph view is completely outside the boundaries of its container.
     * <br>
     * This method calculates the bounds of the graph view and the container, and determines
     * if the graph view is completely outside the container by a margin of 200 units on all sides.
     *
     * @return true if the graph view is completely outside the container bounds, false otherwise
     */
    private boolean isCompletelyOutside() {
        Bounds graphViewBounds = graphView.getBoundsInParent();
        Bounds containerBounds = graphContainer.getBoundsInLocal();
        return   (graphViewBounds.getMaxX() < containerBounds.getMinX() + 200) ||
                (graphViewBounds.getMinX() > containerBounds.getMaxX() - 200) ||
                (graphViewBounds.getMaxY() < containerBounds.getMinY() + 200) ||
                (graphViewBounds.getMinY() > containerBounds.getMaxY() - 200);
    }


    /**
     * Creates a ParallelTransition combining zoom and translation animations for the graph view.
     * <br>
     * This method centers the graph view within its container while applying the provided zoom animation
     * over the specified duration.
     *
     * @param s the duration in seconds for the transition
     * @param zoomTimeline the Timeline for the zoom animation
     * @return a ParallelTransition combining the zoom and translation animations
     */
    private ParallelTransition getParallelTransition(double s, Timeline zoomTimeline) {
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
        return parallelTransition;
    }


}


