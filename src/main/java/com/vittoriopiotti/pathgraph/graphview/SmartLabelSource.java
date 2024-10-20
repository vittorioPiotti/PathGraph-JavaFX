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
 * Method annotation to override an element's label provider.
 * <br/>
 * The annotated method must return a value, otherwise an exception will be thrown.
 * <br/>
 * By default, the text label is obtained from the toString method if this
 * annotation is not present in any other class method; this is also the case
 * with String and other boxed-types, e.g., Integer, Double, etc.
 * <br/>
 * If multiple annotations exist, the behavior is undefined.
 * 
 * @author brunomnsilva
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SmartLabelSource {
    
}
