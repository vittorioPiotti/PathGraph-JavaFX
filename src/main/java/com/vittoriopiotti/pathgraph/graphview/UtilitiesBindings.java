/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.graphview;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;

import static javafx.beans.binding.Bindings.createDoubleBinding;

/**
 * Some {@link Math} operations implemented as bindings.
 * 
 * @author brunomnsilva
 */
public class UtilitiesBindings {
    
    /**
     * Binding for {@link java.lang.Math#atan2(double, double)}
     *
     * @param   y   the ordinate coordinate
     * @param   x   the abscissa coordinate
     * @return  the <i>theta</i> component of the point
     *          (<i>r</i>,&nbsp;<i>theta</i>)
     *          in polar coordinates that corresponds to the point
     *          (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
     */
    public static DoubleBinding atan2(final ObservableDoubleValue y, final ObservableDoubleValue x) {
        return createDoubleBinding(() -> Math.atan2(y.get(), x.get()), y, x);
    }
    
    /**
     * Binding for {@link java.lang.Math#toDegrees(double)}
     *
     * @param   angRad   an angle, in radians
     * @return  the measurement of the angle {@code angRad}
     *          in degrees.
     */
    public static DoubleBinding toDegrees(final ObservableDoubleValue angRad) {
        return createDoubleBinding(() -> Math.toDegrees(angRad.get()), angRad);
    }
}
