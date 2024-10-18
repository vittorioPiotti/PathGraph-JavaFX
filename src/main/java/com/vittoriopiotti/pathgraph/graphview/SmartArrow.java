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

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * A shape of an arrow to be attached to a {@link SmartArrow}.
 *
 * @author brunomnsilva
 * <p>Modified by vittoriopiotti</p>
 */
public class SmartArrow extends Path implements SmartStylableNode {

    public static final int FILL_ARROW = 1;
    public static final int ARROW = 2;



    private final double size;
    private final SmartStyleProxy styleProxy;

    /**
     * Default constructor.
     *
     * @param size determines the size of the arrow (side of the triangle in pixels)
     * @param arrowStyle determines the style of the arrow (filled or unfilled)
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    public SmartArrow(double size, int arrowStyle) {
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, size));
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, -size));

        styleProxy = new SmartStyleProxy(this);
        styleProxy.addStyleClass("edge");
        styleProxy.addStyleClass("arrow");
        this.size = size;
        if (arrowStyle == FILL_ARROW) {
            setFillArrow();
        } else {
            setArrow();
        }

    }

    /**
     * Method to create a filled arrow
     *
     * @author vittoriopiotti
     */
    private void setFillArrow() {
        double arrowWidth = size * 2;
        double arrowHeight = size * 1;
        getElements().clear();
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-arrowWidth, arrowHeight / 2));
        getElements().add(new LineTo(-arrowWidth, -arrowHeight / 2));
        getElements().add(new LineTo(0, 0));
    }

    /**
     * Method to create an unfilled arrow
     *
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void setArrow() {

        getElements().clear();
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, size));
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, -size));
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
