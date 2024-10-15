/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A stylable node can have its css properties changed at runtime.
 * <br>
 * All Java FX nodes used by {@link SmartGraphPanel} to represent graph entities
 * should implement this interface.
 * 
 * @see SmartGraphPanel
 * 
 * @author brunomnsilva
 */
public interface SmartStylableNode {    
    
    /**
     * Applies cumulatively the <code>css</code> inline styles to the node.
     * <br/>
     * These inline JavaFX styles have higher priority and are not overwritten by
     * any css classes set by {@link SmartStylableNode#addStyleClass(java.lang.String) }.
     * But will be discarded if you use  {@link SmartStylableNode#setStyleClass(java.lang.String) }
     * <br/>
     * If you need to clear any previously set inline styles, use 
     * <code>.setStyleInline(null)</code>. Not that this will clear all inline styles previously applied.
     * 
     * @param css styles
     */
    void setStyleInline(String css);
    
    /**
     * Applies the CSS styling defined in class selector <code>cssClass</code>.
     * <br/>
     * The <code>cssClass</code> string must not contain a preceding dot, e.g.,
     * "myClass" instead of ".myClass".
     * <br/>
     * The CSS Class must be defined in <code>smartgraph.css</code> file or
     * in the custom provided stylesheet.
     * <br/>
     * The expected behavior is to remove all current styling before 
     * applying the class css.
     * 
     * @param cssClass name of the CSS class.
     */
    void setStyleClass(String cssClass);
    
    /**
     * Applies cumulatively the CSS styling defined in class selector 
     * <code>cssClass</code>.
     * <br/>
     * The CSS Class must be defined in <code>smartgraph.css</code> file or
     * in the custom provided stylesheet.
     * <br/>
     * The cumulative operation will overwrite any existing styling elements
     * previously defined for previous classes.
     * 
     * @param cssClass name of the CSS class.
     */
    void addStyleClass(String cssClass);
    
    /**
     * Removes a previously <code>cssClass</code> existing CSS styling.
     * <br/>
     * Given styles can be added sequentially, the removal of a css class
     * will be a removal that keeps the previous ordering of kept styles.
     * 
     * @param cssClass name of the CSS class.
     * 
     * @return true if successful; false if <code>cssClass</code> wasn't 
     * previously set.
     */
    boolean removeStyleClass(String cssClass);



}
