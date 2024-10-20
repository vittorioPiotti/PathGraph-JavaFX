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

import java.util.Collection;

/**
 * A directed graph (or digraph) is a graph that is made up of a set of vertices 
 * connected by edges, where the edges have a direction associated with them.
 * <br>
 * A directed-edge leaves the <i>outbound vertex</i>
 * towards the <i>inbound vertex</i> and this changes the reasoning behind some
 * methods of the {@link Graph} interface, which are overridden in this interface
 * to provide different documentation of expected behavior.
 *
 * @param <V> Type of element stored at a vertex
 * @param <E> Type of element stored at an edge
 *
 * @see Graph
 * @see Edge
 * @see Vertex
 *
 * @author brunomsilva
 * <p>Modified by vittoriopiotti</p>
 */
public interface Digraph<V, E> extends Graph<V, E> {
    
    /**
     * Returns a vertex's <i>incident</i> edges as a collection.
     * <br/>
     * Incident edges are all edges that have vertex <code>inbound</code> as the
     * <i>inbound vertex</i>, i.e., the edges "entering" vertex <code>inbound</code>.
     * If there are no incident edges, e.g., an isolated vertex, 
     * returns an empty collection.
     * 
     * @param inbound     vertex for which to obtain the incident edges
     * 
     * @return            collection of edges
     *
     * @author brunomsilva
     */
    @Override
    Collection<Edge<E, V>> incidentEdges(Vertex<V> inbound)
            throws InvalidVertexException;

    /**
     * Returns a vertex's <i>outbound</i> edges as a collection.
     * <br/>
     * Incident edges are all edges that have vertex <code>outbound</code> as the
     * <i>outbound vertex</i>, i.e., the edges "leaving" vertex <code>outbound</code>.
     * If there are no outbound edges, e.g., an isolated vertex, 
     * returns an empty collection.
     * 
     * @param outbound      vertex for which to obtain the outbound edges
     * 
     * @return              collection of edges
     *
     * @throws InvalidVertexException  if <code>outbound</code> is an invalid vertex for the graph
     *
     * @author brunomsilva
     */
    Collection<Edge<E, V>> outboundEdges(Vertex<V> outbound)
            throws InvalidVertexException;
    
    
    /**
     * Evaluates whether two vertices are adjacent, i.e., there exists some
     * directed-edge connecting <code>outbound</code> and <code>inbound</code>.
     * <br/>
     * The existing edge must be directed as <code>outbound --&gt; inbound</code>.
     * <br/>
     * If, for example, there exists only an edge <code>outbound &lt;-- inbound</code>,
     * they are not considered adjacent.
     * 
     * @param outbound      outbound vertex
     * @param inbound       inbound vertex
     * 
     * @return              true if they are adjacent, false otherwise.
     * 
     * @throws InvalidVertexException    if <code>outbound</code> or 
     *                                      <code>inbound</code>
     *                                      are invalid vertices for the graph
     *
     * @author brunomsilva
     */
    @Override
    boolean areAdjacent(Vertex<V> outbound, Vertex<V> inbound)
            throws InvalidVertexException;

    /**
     * Inserts a new edge with a given element between two existing vertices and
     * returns its (the edge's) reference.
     *
     * @param outbound      the outbound vertex from which the edge originates
     * @param inbound       the inbound vertex to which the edge points
     * @param edgeElement   the element to store in the new edge
     * @param cost          the label text associated with the edge
     * @param direction     the direction of the edge (e.g., 0 for bidirectional, 1 for one direction, 2 for the opposite)
     *
     * @return              the reference for the newly created edge
     *
     * @throws InvalidVertexException    if <code>outbound</code> or
     *                                      <code>inbound</code>
     *                                      are invalid vertices for the graph
     *
     * @throws InvalidEdgeException       if there already exists an edge
     *                                      containing <code>edgeElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object)}
     *                                      method.
     *
     * @author brunomsilva
     * <p>Modified by vittoriopiotti</p>
     */
    @Override
    Edge<E, V> insertEdge(Vertex<V> outbound, Vertex<V> inbound, E edgeElement, int cost, int direction)
            throws InvalidVertexException, InvalidEdgeException;


    
    /**
     * Inserts a new edge with a given element between two existing vertices and
     * return its (the edge's) reference.
     * 
     * @param outboundElement   outbound vertex's stored element
     * @param inboundElement    inbound vertex's stored element
     * @param edgeElement       element to store in the new edge
     * @param cost              the label text associated with the edge
     * @param direction         the direction of the edge (e.g., 0 for bidirectional, 1 for one direction, 2 for the opposite)
     *
     * @return                  the reference for the newly created edge
     * 
     * @throws InvalidVertexException    if <code>outboundElement</code> or 
     *                                      <code>inboundElement</code>
     *                                      are not found in any vertices of the graph
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object) }
     *                                      method.
     * 
     * @throws InvalidEdgeException    if there already exists an edge
     *                                      containing <code>edgeElement</code>
     *                                      according to the equality of
     *                                      {@link Object#equals(java.lang.Object) }
     *                                      method.
     *
     * @author brunomsilva
     * <p>Modified by vittoriopiotti</p>
     */
    @Override
    Edge<E, V> insertEdge(V outboundElement, V inboundElement, E edgeElement,int cost,int direction)
            throws InvalidVertexException, InvalidEdgeException;



}
