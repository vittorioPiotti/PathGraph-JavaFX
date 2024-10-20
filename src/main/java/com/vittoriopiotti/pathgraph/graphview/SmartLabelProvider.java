/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A provider interface for generating labels.
 *
 * @param <T> the type of the elements for which labels are generated
 *
 * @author brunomnsilva
 */
public interface SmartLabelProvider<T> {

    /**
     * Returns the label for the specified element.
     * <br/>
     * The returned value is expected to be non-null.
     *
     * @param element the element for which the label is generated
     * @return the label for the specified element
     */
    String valueFor(T element);
}