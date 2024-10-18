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

package com.vittoriopiotti.pathgraph.graph;
import com.vittoriopiotti.pathgraph.app.Constants;

import java.util.Collection;

/**
 * A graph is made up of a set of vertices connected by edges, where the edges 
 * have no direction associated with them, i.e., they establish a two-way connection.
 *
 * @param <V> Type of element stored at a vertex
 * @param <E> Type of element stored at an edge
 * 
 * @see Edge
 * @see Vertex
 *
 *  @author brunomnsilva
 *  <p>Modified by vittoriopiotti</p>
 */
public interface Graph<V, E> {

    /**
     * Returns the total number of vertices of the graph.
     * 
     * @return      total number of vertices
     *
     * @author brunomnsilva
     */
    int numVertices();

    /**
     * Returns the total number of edges of the graph.
     * 
     * @return      total number of vertices
     *
     * @author brunomnsilva
     */
    int numEdges();

    /**
     * Returns the vertices of the graph as a collection.
     * <br/>
     * If there are no vertices, returns an empty collection.
     * 
     * @return      collection of vertices
     *
     * @author brunomnsilva
     */
    Collection<Vertex<V>> vertices();

    /**
     * Returns the edges of the graph as a collection.
     * <br/>
     * If there are no edges, returns an empty collection.
     * 
     * @return      collection of edges
     *
     * @author brunomnsilva
     */
    Collection<Edge<E, V>> edges();

    /**
     * Returns a vertex's <i>incident</i> edges as a collection.
     * <br/>
     * Incident edges are all edges that are connected to vertex <code>v</code>.
     * If there are no incident edges, e.g., an isolated vertex, 
     * returns an empty collection.
     * 
     * @param v     vertex for which to obtain the incident edges
     * 
     * @return      collection of edges
     *
     * @throws InvalidVertexException    if the vertex is invalid for the graph
     *
     * @author brunomnsilva
     */
    Collection<Edge<E, V>> incidentEdges(Vertex<V> v)
            throws InvalidVertexException;

    /**
     * Given vertex <code>v</code>, return the opposite vertex at the other end
     * of edge <code>e</code>.
     * <br/>
     * If both <code>v</code> and <code>e</code> are valid, but <code>e</code>
     * is not connected to <code>v</code>, returns <i>null</i>.
     * 
     * @param v         vertex on one end of <code>e</code>
     * @param e         edge connected to <code>v</code>
     * @return          opposite vertex along <code>e</code>
     * 
     * @throws InvalidVertexException    if the vertex is invalid for the graph
     * @throws InvalidEdgeException      if the edge is invalid for the graph
     *
     * @author brunomnsilva
     */
    Vertex<V> opposite(Vertex<V> v, Edge<E, V> e)
            throws InvalidVertexException, InvalidEdgeException;

    /**
     * Evaluates whether two vertices are adjacent, i.e., there exists some
     * edge connecting <code>u</code> and <code>v</code>.
     * 
     * @param u     a vertex
     * @param v     another vertex
     * 
     * @return      true if they are adjacent, false otherwise.
     * 
     * @throws InvalidVertexException    if <code>u</code> or <code>v</code>
     *                                      are invalid vertices for the graph
     *
     * @author brunomnsilva
     */
    boolean areAdjacent(Vertex<V> u, Vertex<V> v)
            throws InvalidVertexException;

    /**
     * Inserts a new vertex with a given element, returning its reference.
     * 
     * @param vElement      the element to store at the vertex
     * 
     * @return              the reference of the newly created vertex
     * 
     * @throws InvalidVertexException    if there already exists a vertex
     *                                      containing <code>vElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object) }
     *                                      method.
     *
     * @author brunomnsilva
     */
    Vertex<V> insertVertex(V vElement)
            throws InvalidVertexException;

    /**
     * Inserts a new edge with a given element between two existing vertices and
     * returns its (the edge's) reference.
     *
     * @param u             a vertex
     * @param v             another vertex
     * @param edgeElement   the element to store in the new edge
     * @param cost          the cost associated with the edge
     * @param direction     the direction of the edge, which can be one of:
     *                      {@link Constants#BIDIRECTIONAL},
     *                      {@link Constants#DIRECTION_FIRST},
     *                      or {@link Constants#DIRECTION_SECOND}
     *
     * @return              the reference for the newly created edge
     *
     * @throws InvalidVertexException    if <code>u</code> or <code>v</code>
     *                                      are invalid vertices for the graph
     *
     * @throws InvalidEdgeException       if there already exists an edge
     *                                      containing <code>edgeElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object)}
     *                                      method.
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    Edge<E, V> insertEdge(Vertex<V> u, Vertex<V> v, E edgeElement,int cost,int direction)
            throws InvalidVertexException, InvalidEdgeException;


    /**
     * Inserts a new edge with a given element between two existing vertices and
     * returns its (the edge's) reference.
     *
     * @param vElement1     a vertex's stored element
     * @param vElement2     another vertex's stored element
     * @param edgeElement   the element to store in the new edge
     * @param cost          the cost associated with the edge
     * @param direction     the direction of the edge, which can be one of:
     *                      {@link Constants#BIDIRECTIONAL},
     *                      {@link Constants#DIRECTION_FIRST},
     *                      or {@link Constants#DIRECTION_SECOND}
     *
     * @return              the reference for the newly created edge
     *
     * @throws InvalidVertexException    if <code>vElement1</code> or
     *                                      <code>vElement2</code>
     *                                      are not found in any vertices of the graph
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object)}
     *                                      method.
     *
     * @throws InvalidEdgeException       if there already exists an edge
     *                                      containing <code>edgeElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object)}
     *                                      method.
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    Edge<E, V> insertEdge(V vElement1, V vElement2, E edgeElement,int cost,int direction)
            throws InvalidVertexException, InvalidEdgeException;

    /**
     * Removes a vertex, along with all of its incident edges, and returns the element
     * stored at the removed vertex.
     * 
     * @param v     vertex to remove
     * 
     * @return      element stored at the removed vertex
     * 
     * @throws InvalidVertexException if <code>v</code> is an invalid vertex for the graph
     *
     * @author brunomnsilva
     */
    V removeVertex(Vertex<V> v) throws InvalidVertexException;

    /**
     * Removes an edge and return its element.
     * 
     * @param e     edge to remove
     * 
     * @return      element stored at the removed edge
     * 
     * @throws InvalidEdgeException if <code>e</code> is an invalid edge for the graph.
     *
     * @author brunomnsilva
     */
    E removeEdge(Edge<E, V> e) throws InvalidEdgeException;
    
    /**
     * Replaces the element of a given vertex with a new element and returns the
     * previous element stored at <code>v</code>.
     * 
     * @param v             vertex to replace its element
     * @param newElement    new element to store in <code>v</code>
     * 
     * @return              previous element previously stored in <code>v</code>
     * 
     * @throws InvalidVertexException    if the vertex <code>v</code> is invalid for the graph, or;
     *                                      if there already exists another vertex containing
     *                                      the element <code>newElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object) }
     *                                      method.
     *
     * @author brunomnsilva
     */
    V replace(Vertex<V> v, V newElement) throws InvalidVertexException;
    
    /**
     * Replaces the element of a given edge with a new element and returns the
     * previous element stored at <code>e</code>.
     * 
     * @param e             edge to replace its element
     * @param newElement    new element to store in <code>e</code>
     * 
     * @return              previous element previously stored in <code>e</code>
     * 
     * @throws InvalidEdgeException    if the edge <code>e</code> is invalid for the graph, or;
     *                                      if there already exists another edge containing
     *                                      the element <code>newElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object)} 
     *                                      method.
     *
     * @author brunomnsilva
     */
    E replace(Edge<E, V> e, E newElement) throws InvalidEdgeException;
}
