/*
 * The MIT License
 *
 * JavaFXSmartGraph | Copyright 2019-2024  brunomnsilva@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.vittoriopiotti.pathgraph.graph;

/**
 * An edge connects two {@link Vertex} of type <code>V</code> and stores
 * an element of type <code>E</code>.
 * <br/>
 * The edge may be used in oriented and non-oriented graphs.
 * 
 * @param <E> Type of value stored in the edge
 * @param <V> Type of value stored in the vertices that this edge connects.
 * 
 * @see Graph
 * @see Digraph
 */
public interface Edge<E, V> {
    
    /**
     * Returns the element stored in the edge.
     * 
     * @return      stored element
     */
    E element();
    
    /**
     * Returns and array of size 2, with references for both vertices at the ends
     * of an edge.
     * <br/>
     * In a {@link Digraph} the reference at {@code vertices()[0]} must be that
     * of the <i>outbound vertex</i> and at {@code vertices()[1]} that of the <i>inbound</i>
     * vertex.
     * 
     * @return      an array of length 2, containing the vertices at both ends.
     */
    Vertex<V>[] vertices();


    /**
     * Returns the cost associated with this edge.
     *
     * @return the cost of the edge
     *
     * @author vittoriopiotti
     */
    int getCost();


    /**
     * Returns the direction of the edge.
     * <br/>
     * In a directed graph, this value can be used to determine if the edge
     * points from the first vertex to the second or vice versa.
     *
     * @return the direction of the edge
     *
     * @author vittoriopiotti
     */
    int getDirection();



    /**
     * Sets the direction of the edge.
     * <br/>
     * This method allows to specify the direction the edge should have.
     * In a directed graph, this typically determines which vertex is the start
     * and which is the end.
     *
     * @param direction the direction to set for the edge
     *
     * @author vittoriopiotti
     */
    void setDirection(int direction);
}
