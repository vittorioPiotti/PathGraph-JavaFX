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

import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import com.vittoriopiotti.pathgraph.graph.Edge;
import javafx.beans.binding.Bindings;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;


/**
 * Implementation of a straight line edge.
 *
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 *
 * @author brunomnsilva
 * <p>Modified by vittoriopiotti</p>
 */
public class SmartGraphEdgeLine<E, V> extends Line implements SmartGraphEdgeBase<E, V> {

    private final Edge<E, V> underlyingEdge;
    private int cost;

    private final SmartGraphVertexNode<V> inbound;
    private final SmartGraphVertexNode<V> outbound;

    private SmartLabel attachedLabel = null;
    private SmartArrow attachedArrow = null;
    private Rectangle attachedBackground = null;

    private int direction;
    /* Styling proxy */
    private final SmartStyleProxy styleProxy;

    @Override
    public Rectangle getAttachedBackground(){
        return attachedBackground;
    }
    @Override
    public void attachBackground(Rectangle attachedBackground){
        this.attachedBackground = attachedBackground;
    }
    @Override
    public int getDirection(){
        return direction;
    }

    @Override
    public void setDirection(int direction){
        this.direction = direction;
    }
    /**
     * Constructs a SmartGraphEdgeLine representing an edge between two SmartGraphVertexNodes.
     *
     * @param edge     the edge associated with this line
     * @param inbound  the inbound SmartGraphVertexNode
     * @param outbound the outbound SmartGraphVertexNode
     */
    public SmartGraphEdgeLine(Edge<E, V> edge, SmartGraphVertexNode<V> inbound, SmartGraphVertexNode<V> outbound, int cost, int direction) {
        if( inbound == null || outbound == null) {
            throw new IllegalArgumentException("Cannot connect null vertices.");
        }
        this.direction = direction;
        this.inbound = inbound;
        this.outbound = outbound;
        this.cost = cost;
        this.underlyingEdge = edge;

        styleProxy = new SmartStyleProxy(this);
        styleProxy.addStyleClass("edge");

        this.startXProperty().bind(outbound.centerXProperty());
        this.startYProperty().bind(outbound.centerYProperty());
        this.endXProperty().bind(inbound.centerXProperty());
        this.endYProperty().bind(inbound.centerYProperty());

        propagateHoverEffectToArrow();


    }
    @Override
    public void setStyleInline(String css) {
        styleProxy.setStyleInline(css);
        if(attachedArrow != null) {
            attachedArrow.setStyleInline(css);
        }
    }

    @Override
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
        if(attachedArrow != null) {
            attachedArrow.setStyleClass(cssClass);
        }
    }

    @Override
    public SmartGraphVertexNode<V> getInbound(){
        return this.inbound;
    }

    @Override
    public SmartGraphVertexNode<V> getOutbound(){
        return this.outbound;
    }
    @Override
    public void addStyleClass(String cssClass) {
        styleProxy.addStyleClass(cssClass);
        if(attachedArrow != null) {
            attachedArrow.addStyleClass(cssClass);
        }
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        boolean result = styleProxy.removeStyleClass(cssClass);
        if(attachedArrow != null) {
            attachedArrow.removeStyleClass(cssClass);
        }
        return result;
    }

    @Override
    public void attachLabel(SmartLabel label) {
        this.attachedLabel = label;

        label.xProperty().bind(startXProperty().add(endXProperty()).divide(2).subtract(Bindings.divide(label.layoutWidthProperty(), 2)));
        label.yProperty().bind(startYProperty().add(endYProperty()).divide(2).add(Bindings.divide(label.layoutHeightProperty(), 1.5)));
    }

    @Override
    public SmartLabel getAttachedLabel() {
        return attachedLabel;
    }

    @Override
    public Edge<E, V> getUnderlyingEdge() {
        return underlyingEdge;
    }
    @Override
    public void setOnMouseClicked_(EventHandler<? super MouseEvent> value) {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, value);
    }


        @Override
    public void attachArrow(SmartArrow arrow) {
        this.attachedArrow = arrow;


        /* attach arrow to line's endpoint */
        arrow.translateXProperty().bind(endXProperty());
        arrow.translateYProperty().bind(endYProperty());

        /* rotate arrow around itself based on this line's angle */
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(translateXProperty());
        rotation.pivotYProperty().bind(translateYProperty());
        rotation.angleProperty().bind( UtilitiesBindings.toDegrees(
                UtilitiesBindings.atan2( endYProperty().subtract(startYProperty()),
                endXProperty().subtract(startXProperty()))
        ));

        arrow.getTransforms().add(rotation);

        /* add translation transform to put the arrow touching the circle's bounds */
        Translate t = new Translate(0, 0);
        t.xProperty().bind( inbound.radiusProperty().negate() );

        arrow.setOnMouseEntered(e -> {
            this.applyHoverStyle();
            attachedLabel.applyHoverStyle();
        });
        arrow.setOnMouseExited(e -> {
            this.applyDefaultStyle();
            attachedLabel.applyDefaultStyle();
        });



        arrow.getTransforms().add(t);
    }



    @Override
    public SmartArrow getAttachedArrow() {
         return this.attachedArrow;
    }

    @Override
    public SmartStylableNode getStylableArrow() {
        return this.attachedArrow;
    }

    @Override
    public SmartStylableNode getStylableLabel() {
        return this.attachedLabel;
    }

    @Override
    public void applyDashStyle() {


        if (!getStyleClass().contains("edge-dash")) {
            getStyleClass().remove("edge");
            getStyleClass().remove("edge-hover");
            getStyleClass().remove("edge-dash-hover");
            getStyleClass().add("edge-dash");

        }
    }

    @Override
    public void resetStyle(){



        if (!getStyleClass().contains("edge")) {
            getStyleClass().remove("edge-dash");
            getStyleClass().remove("edge-dash-hover");
            getStyleClass().remove("edge-hover");
            getStyleClass().add("edge");

        }

        if (!attachedArrow.getStyleClass().contains("arrow")) {
            // Remove hover style class if it exists
            attachedArrow.getStyleClass().remove("arrow-hover");

            // Add default style class
            attachedArrow.getStyleClass().add("arrow");
        }


        if (!attachedLabel.getStyleClass().contains("edge-label")) {
            // Remove hover style class if it exists
            attachedLabel.getStyleClass().remove("edge-label-hover");

            // Add default style class
            attachedLabel.getStyleClass().add("edge-label");
        }
    }



    @Override
    public void applyHoverStyle() {
        if(getStyleClass().contains("edge")) {

            // Check if the current style class is not already 'edge-hover'
            if (!getStyleClass().contains("edge-hover")) {
                // Remove default style class if it exists
                getStyleClass().remove("edge");

                // Add hover style class
                getStyleClass().add("edge-hover");
            }

            if (!attachedArrow.getStyleClass().contains("arrow-hover")) {
                // Remove hover style class if it exists
                attachedArrow.getStyleClass().remove("arrow");

                // Add default style class
                attachedArrow.getStyleClass().add("arrow-hover");
            }

            if (!attachedLabel.getStyleClass().contains("edge-label-hover")) {
                // Remove hover style class if it exists
                attachedLabel.getStyleClass().remove("edge-label");

                // Add default style class
                attachedLabel.getStyleClass().add("edge-label-hover");
            }
        }else   if(getStyleClass().contains("edge-dash")) {

            // Check if the current style class is not already 'edge-hover'
            if (!getStyleClass().contains("edge-dash-hover")) {
                // Remove default style class if it exists
                getStyleClass().remove("edge-dash");

                // Add hover style class
                getStyleClass().add("edge-dash-hover");
            }

            if (!attachedArrow.getStyleClass().contains("arrow-hover")) {
                // Remove hover style class if it exists
                attachedArrow.getStyleClass().remove("arrow");

                // Add default style class
                attachedArrow.getStyleClass().add("arrow-hover");
            }

            if (!attachedLabel.getStyleClass().contains("edge-label-hover")) {
                // Remove hover style class if it exists
                attachedLabel.getStyleClass().remove("edge-label");

                // Add default style class
                attachedLabel.getStyleClass().add("edge-label-hover");
            }
        }

    }

    @Override
    public void applyDefaultStyle() {

        if(getStyleClass().contains("edge-hover")) {
            // Check if the current style class is not already 'edge'
            if (!getStyleClass().contains("edge")) {
                // Remove hover style class if it exists
                getStyleClass().remove("edge-hover");

                // Add default style class
                getStyleClass().add("edge");
            }
            // Sovrascrivi lo stile inline
            if (!attachedArrow.getStyleClass().contains("arrow")) {
                // Remove hover style class if it exists
                attachedArrow.getStyleClass().remove("arrow-hover");

                // Add default style class
                attachedArrow.getStyleClass().add("arrow");
            }


            if (!attachedLabel.getStyleClass().contains("edge-label")) {
                // Remove hover style class if it exists
                attachedLabel.getStyleClass().remove("edge-label-hover");

                // Add default style class
                attachedLabel.getStyleClass().add("edge-label");
            }
        }else   if(getStyleClass().contains("edge-dash-hover")) {
            // Check if the current style class is not already 'edge'
            if (!getStyleClass().contains("edge-dash")) {
                // Remove hover style class if it exists
                getStyleClass().remove("edge-dash-hover");

                // Add default style class
                getStyleClass().add("edge-dash");
            }
            // Sovrascrivi lo stile inline
            if (!attachedArrow.getStyleClass().contains("arrow")) {
                // Remove hover style class if it exists
                attachedArrow.getStyleClass().remove("arrow-hover");

                // Add default style class
                attachedArrow.getStyleClass().add("arrow");
            }


            if (!attachedLabel.getStyleClass().contains("edge-label")) {
                // Remove hover style class if it exists
                attachedLabel.getStyleClass().remove("edge-label-hover");

                // Add default style class
                attachedLabel.getStyleClass().add("edge-label");
            }
        }
    }

    @Override
    public int getCost(){
        return cost;
    }

    @Override
    public void setCost(int text){
        cost = text;
        attachedLabel.setText_(String.valueOf(cost));
    }



    /**
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void propagateHoverEffectToArrow() {
        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(attachedArrow != null && newValue) {
                attachedLabel.applyHoverStyle();
                attachedArrow.fireEvent(new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0, true, true, true, true, true, true, true, true, true, true, null));
            } else if(attachedArrow != null) { //newValue is false, hover ended
                attachedLabel.applyDefaultStyle();
                attachedArrow.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.NONE, 0, true, true, true, true, true, true, true, true, true, true, null));

            }
        });
    }

}
