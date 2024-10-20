/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.graphview;

import javafx.scene.shape.Shape;

/**
 * This class acts as a proxy for styling of nodes.
 * <br/>
 * It essentially groups all the logic, avoiding code duplicate.
 * <br/>
 * Classes that have this behavior can delegate the method calls to an instance
 * of this class.
 * 
 * @author brunomnsilva
 */
public class SmartStyleProxy implements SmartStylableNode {

    private Shape client;

    /**
     * Creates a new style proxy for a shape client.
     * @param client the shape client
     */
    public SmartStyleProxy(Shape client) {
        this.client = client;
    }

    /**
     * Changes the shape client of this proxy.
     * @param client the new shape client
     */
    public void setClient(Shape client) {
        this.client = client;
    }

    @Override
    public void setStyleInline(String css) {
        client.setStyle(css);
    }

    @Override
    public void setStyleClass(String cssClass) {
        client.getStyleClass().clear();
        client.setStyle(null);
        client.getStyleClass().add(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        client.getStyleClass().add(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return client.getStyleClass().remove(cssClass);
    }


    /**
     * Copies all the styles and classes (currently applied) of <code>source</code> to <code>destination</code>.
     * @param source the shape whose styles are to be copied
     * @param destination the shape that receives the copied styles
     */
    protected static void copyStyling(Shape source, Shape destination) {
        destination.setStyle(source.getStyle());
        destination.getStyleClass().addAll(source.getStyleClass());
    }


}
