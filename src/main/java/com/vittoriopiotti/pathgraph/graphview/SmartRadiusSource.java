/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation to override an element's vertex shape radius.
 * <br/>
 * The annotated method must return a value (Double) with a non-negative value, otherwise an exception will be thrown.
 * <br/>
 * By default, the shape radius is defined in "smartgraph.properties" or in a custom properties file.
 * <br/>
 * If multiple annotations exist, the behavior is undefined.
 * 
 * @author brunomnsilva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SmartRadiusSource {
    
}
