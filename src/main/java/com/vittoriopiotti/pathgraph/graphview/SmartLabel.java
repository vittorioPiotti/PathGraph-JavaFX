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


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.text.Text;

/**
 * A label contains text and can be attached to any {@link SmartLabelledNode}.
 * <br>
 * This class extends from {@link Text} and is allowed any corresponding
 * css formatting.
 * 
 * @author brunomnsilva
 * <p>Modified by vittoriopiotti</p>
 */

public class SmartLabel extends Text implements SmartStylableNode {

    private final SmartStyleProxy styleProxy;

    private final DoubleProperty layoutWidth;
    private final DoubleProperty layoutHeight;

    /**
     * Default constructor.
     * @param text the text of the SmartLabel.
     */
    public SmartLabel(String text) {
        this(0, 0, text);
    }

    /**
     * Constructor that accepts an initial position.
     * @param x initial x coordinate
     * @param y initial y coordinate
     * @param text the text of the SmartLabel.
     */
    public SmartLabel(double x, double y, String text) {
        super(x, y, text);
        styleProxy = new SmartStyleProxy(this);

        this.layoutWidth = new SimpleDoubleProperty(  );
        this.layoutHeight = new SimpleDoubleProperty(  );

        layoutBoundsProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                if(Double.compare(layoutWidth.doubleValue(), newValue.getWidth()) != 0) {
                    layoutWidth.set(newValue.getWidth());
                }
                if(Double.compare(layoutHeight.doubleValue(), newValue.getHeight()) != 0) {
                    layoutHeight.set(newValue.getHeight());
                }
            }
        });
    }
    /**
     * Applies the hover style to the label.
     * It removes the default style and adds the hover style class.
     *
     * @author vittoriopiotti
     */
    public void applyHoverStyle() {
        // Check if the current style class is not already 'edge-label-hover'
        if (!getStyleClass().contains("edge-label-hover") && getStyleClass().contains("edge-label")) {
            // Remove default style class if it exists
            getStyleClass().remove("edge-label");

            // Add hover style class
            getStyleClass().add("edge-label-hover");
        }
    }

    /**
     * Applies the default style to the label.
     * It removes the hover style and adds the default style class.
     *
     * @author vittoriopiotti
     */
    public void applyDefaultStyle() {
        // Check if the current style class is not already 'edge-label'
        if (!getStyleClass().contains("edge-label") && getStyleClass().contains("edge-label-hover")) {
            // Remove hover style class if it exists
            getStyleClass().remove("edge-label-hover");

            // Add default style class
            getStyleClass().add("edge-label");
        }
    }
    /**
     * Returns the read-only property representing the layout width of this label.
     *
     * @return the read-only property representing the layout width
     */
    public ReadOnlyDoubleProperty layoutWidthProperty() {
        return layoutWidth;
    }

    /**
     * Returns the read-only property representing the layout height of this label.
     *
     * @return the read-only property representing the layout height
     */
    public ReadOnlyDoubleProperty layoutHeightProperty() {
        return layoutHeight;
    }

    /**
     * Use instead of {@link #setText(String)} to allow for correct layout adjustments and label placement.
     * @param text the text to display on the label
     */
    public void setText_(String text) {
        if(getText().compareTo(text) != 0) {
            setText(text);
        }
    }

    @Override
    public void setStyleInline(String css) {
        styleProxy.setStyleInline(css);
    }

    @Override
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        styleProxy.addStyleClass(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return styleProxy.removeStyleClass(cssClass);
    }

}




