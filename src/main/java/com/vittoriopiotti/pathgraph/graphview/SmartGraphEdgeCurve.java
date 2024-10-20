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

import com.vittoriopiotti.pathgraph.app.Constants;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;
import com.vittoriopiotti.pathgraph.graph.Edge;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.function.BiConsumer;

/**
 * Concrete implementation of a curved edge.
 * <br>
 * The edge binds its start point to the <code>outbound</code>
 * {@link SmartGraphVertexNode} center and its end point to the
 * <code>inbound</code> {@link SmartGraphVertexNode} center. As such, the curve
 * is updated automatically as the vertices move.
 * <br>
 * Given there can be several curved edges connecting two vertices, when calling
 * the constructor {@link (Edge,
 * SmartGraphVertexNode ,
 * SmartGraphVertexNode , int) } the <code>edgeIndex</code>
 * can be specified as to create non-overlapping curves.
 *
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 *
 * @author brunomnsilva
 * <p>Modified by vittoriopiotti</p>
 */
public class SmartGraphEdgeCurve<E, V> extends CubicCurve implements SmartGraphEdgeBase<E, V> {

    private static final double MAX_EDGE_CURVE_ANGLE = 45;
    private static final double MIN_EDGE_CURVE_ANGLE = 3;

    /** Distance (in pixels) that establishes the maximum curve threshold */
    public static final int DISTANCE_THRESHOLD = 400;

    /** Radius applied to loop curves */
    public static final int LOOP_RADIUS_FACTOR = 4;

    private final Edge<E, V> underlyingEdge;


    private int cost = 0;

    private final SmartGraphVertexNode<V> inbound;
    private final SmartGraphVertexNode<V> outbound;
    private SmartLabel attachedLabel = null;
    private SmartArrow attachedArrow = null;
    private Rectangle attachedBackground = null;


    /* Styling proxy */
    private final SmartStyleProxy styleProxy;


    private int direction = Constants.NATURAL_DIRECTION;







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
     * Constructs a SmartGraphEdgeCurve representing a curved edge between two SmartGraphVertexNodes.
     *
     * @param edge     the edge associated with this curve
     * @param inbound  the inbound SmartGraphVertexNode
     * @param outbound the outbound SmartGraphVertexNode
     */
    public SmartGraphEdgeCurve(Edge<E, V> edge, SmartGraphVertexNode<V> inbound, SmartGraphVertexNode<V> outbound, BiConsumer<MouseEvent, Edge<E, V>> onClickArrow, int cost, int direction) {
        this(edge, inbound, outbound, 0,cost,direction);
    }


    /**
     * Constructs a SmartGraphEdgeCurve representing an edge curve between two SmartGraphVertexNodes.
     *
     * @param edge     the edge associated with this curve
     * @param inbound  the inbound SmartGraphVertexNode
     * @param outbound the outbound SmartGraphVertexNode
     * @param edgeIndex the edge index (>=0)
     */
    public SmartGraphEdgeCurve(Edge<E, V> edge, SmartGraphVertexNode<V> inbound, SmartGraphVertexNode<V> outbound, int edgeIndex, int cost, int direction) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.underlyingEdge = edge;
        this.cost = cost;
        this.direction = direction;
        styleProxy = new SmartStyleProxy(this);
        styleProxy.addStyleClass("edge");

        //bind start and end positions to vertices centers through properties
        this.startXProperty().bind(outbound.centerXProperty());
        this.startYProperty().bind(outbound.centerYProperty());
        this.endXProperty().bind(inbound.centerXProperty());
        this.endYProperty().bind(inbound.centerYProperty());

        //TODO: improve this solution taking into account even indices, etc.

        //update();
        enableListeners();

        propagateHoverEffectToArrow();

    }

    public void setStyleInline(String css) {
        styleProxy.setStyleInline(css);
        if(attachedArrow != null) {
            attachedArrow.setStyleInline(css);
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
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
        if(attachedArrow != null) {
            attachedArrow.setStyleClass(cssClass);
        }
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
    public int getCost(){
        return cost;
    }

    @Override
    public void setCost(int text){
        cost = text;
        attachedLabel.setText_(String.valueOf(cost));
    }
    private void update() {

        if (inbound == outbound) {
            /* Make a loop using the control points proportional to the vertex radius */

            //TODO: take into account several "self-loops" with randomAngleFactor
            double midpointX1 = outbound.getCenterX() - inbound.getRadius() * LOOP_RADIUS_FACTOR;
            double midpointY1 = outbound.getCenterY() - inbound.getRadius() * LOOP_RADIUS_FACTOR;

            double midpointX2 = outbound.getCenterX() + inbound.getRadius() * LOOP_RADIUS_FACTOR;
            double midpointY2 = outbound.getCenterY() - inbound.getRadius() * LOOP_RADIUS_FACTOR;

            setControlX1(midpointX1);
            setControlY1(midpointY1);
            setControlX2(midpointX2);
            setControlY2(midpointY2);

        } else {
            /* Make a curved edge. The curvature is bounded and proportional to the distance;
                higher curvature for closer vertices  */

            Point2D startpoint = new Point2D(inbound.getCenterX(), inbound.getCenterY());
            Point2D endpoint = new Point2D(outbound.getCenterX(), outbound.getCenterY());

            double distance = startpoint.distance(endpoint);



            double angle = linearDecay(MAX_EDGE_CURVE_ANGLE, MIN_EDGE_CURVE_ANGLE, distance, DISTANCE_THRESHOLD);

            Point2D midpoint = UtilitiesPoint2D.calculateTriangleBetween(startpoint, endpoint,
                    angle/2);

            setControlX1(midpoint.getX());
            setControlY1(midpoint.getY());
            setControlX2(midpoint.getX());
            setControlY2(midpoint.getY());
        }
    }

    /**
     * Provides the decreasing linear function decay.
     * @param initialValue initial value
     * @param finalValue maximum value
     * @param distance current distance
     * @param distanceThreshold distance threshold (maximum distance -> maximum value)
     * @return the decay function value for <code>distance</code>
     */
    @SuppressWarnings("all")
    private static double linearDecay(double initialValue, double finalValue, double distance, double distanceThreshold) {
        //Args.requireNonNegative(distance, "distance");
        //Args.requireNonNegative(distanceThreshold, "distanceThreshold");
        // Parameters are internally guaranteed to be positive. We avoid two method calls.

        if(distance >= distanceThreshold) return finalValue;

        return initialValue + (finalValue - initialValue) * distance / distanceThreshold;
    }

    private void enableListeners() {
        // With a curved edge we need to continuously update the control points.
        // TODO: Maybe we can achieve this solely with bindings? Maybe there's no performance gain in doing so.

        this.startXProperty().addListener((ov, oldValue, newValue) -> {
            update();
        });
        this.startYProperty().addListener((ov, oldValue, newValue) -> {
            update();
        });
        this.endXProperty().addListener((ov, oldValue, newValue) -> {
            update();
        });
        this.endYProperty().addListener((ov, oldValue, newValue) -> {
            update();
        });
    }

    @Override
    public void attachLabel(SmartLabel label) {
        this.attachedLabel = (SmartLabel) label;

        label.xProperty().bind(controlX1Property().add(controlX2Property()).divide(2).subtract(Bindings.divide(label.layoutWidthProperty(),2)));
        label.yProperty().bind(controlY1Property().add(controlY2Property()).divide(2).add(Bindings.divide(label.layoutHeightProperty(), 2)));
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
    public void attachArrow(SmartArrow arrow) {
        this.attachedArrow = arrow;
        Rotate rotation;
        if(direction == Constants.NATURAL_DIRECTION){
            /* attach arrow to line's endpoint */
            arrow.translateXProperty().bind(startXProperty());
            arrow.translateYProperty().bind(startYProperty());

            /* rotate arrow around itself based on this line's angle */
            rotation = new Rotate();
            rotation.pivotXProperty().bind(translateXProperty());
            rotation.pivotYProperty().bind(translateYProperty());
            rotation.angleProperty().bind(UtilitiesBindings.toDegrees(
                    UtilitiesBindings.atan2(startYProperty().subtract(controlY2Property()),
                            startXProperty().subtract(controlX2Property()))
            ));
        }else if(direction == Constants.OPPOSITE_DIRECTION){
            /* attach arrow to line's endpoint */
            arrow.translateXProperty().bind(endXProperty());
            arrow.translateYProperty().bind(endYProperty());

            /* rotate arrow around itself based on this line's angle */
            rotation = new Rotate();
            rotation.pivotXProperty().bind(translateXProperty());
            rotation.pivotYProperty().bind(translateYProperty());
            rotation.angleProperty().bind(UtilitiesBindings.toDegrees(
                    UtilitiesBindings.atan2(endYProperty().subtract(controlY2Property()),
                            endXProperty().subtract(controlX2Property()))
            ));
        }else{

            rotation = new Rotate();

        }

        arrow.getTransforms().add(rotation);
        Translate t = new Translate(0, 0);
        t.xProperty().bind( inbound.radiusProperty().negate() );
        arrow.getTransforms().add(t);
        arrow.setStyleClass("edge");
        arrow.addStyleClass("arrow");

        arrow.setOnMouseEntered(e -> {
            this.applyHoverStyle();
            attachedLabel.applyHoverStyle();
        });
        arrow.setOnMouseExited(e -> {
            this.applyDefaultStyle();
            attachedLabel.applyDefaultStyle();
        });

    }


    @Override
    public void setOnMouseClicked_(EventHandler<? super MouseEvent> value) {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, value);
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


    /**
     * @author brunomnsilva
     * <p>Modified by vittoriopiotti</p>
     */
    private void propagateHoverEffectToArrow() {
        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if(attachedArrow != null && newValue) {
                attachedLabel.applyHoverStyle();
                attachedLabel.getScene().setCursor(Cursor.HAND);


                attachedArrow.fireEvent(new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.NONE, 0, true, true, true, true, true, true, true, true, true, true, null));

            } else if(attachedArrow != null) { //newValue is false, hover ended
                attachedLabel.applyDefaultStyle();
                attachedLabel.getScene().setCursor(Cursor.DEFAULT);

                attachedArrow.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.NONE, 0, true, true, true, true, true, true, true, true, true, true, null));

            }
        });
    }
}
