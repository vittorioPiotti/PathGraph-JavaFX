/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

/**
 * A node to which a {@link SmartLabel} can be attached.
 * 
 * @author brunomnsilva
 */
public interface SmartLabelledNode {
    
    /**
     * Own and bind the <code>label</code> position to the desired position.
     * 
     * @param label     text label node
     */
    void attachLabel(SmartLabel label);
    
    /**
     * Returns the attached text label, if any.
     * 
     * @return      the text label reference or null if no label is attached
     */
    SmartLabel getAttachedLabel();
    
}
