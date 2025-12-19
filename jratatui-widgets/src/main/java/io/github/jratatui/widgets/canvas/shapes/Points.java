/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.canvas.shapes;

import io.github.jratatui.style.Color;
import io.github.jratatui.widgets.canvas.Painter;
import io.github.jratatui.widgets.canvas.Shape;

/**
 * A collection of points (scatter plot).
 *
 * <pre>{@code
 * double[][] coords = {{10, 20}, {30, 40}, {50, 60}};
 * context.draw(new Points(coords, Color.YELLOW));
 * }</pre>
 *
 * @see Shape
 */
public record Points(double[][] coords, Color color) implements Shape {

    /**
     * Creates a points shape from coordinate pairs and a color.
     */
    public static Points of(double[][] coords, Color color) {
        return new Points(coords, color);
    }

    /**
     * Creates a points shape from x and y arrays.
     *
     * @param x     array of x coordinates
     * @param y     array of y coordinates
     * @param color the color for all points
     * @return a new Points shape
     * @throws IllegalArgumentException if arrays have different lengths
     */
    public static Points of(double[] x, double[] y, Color color) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y arrays must have the same length");
        }
        double[][] coords = new double[x.length][2];
        for (int i = 0; i < x.length; i++) {
            coords[i][0] = x[i];
            coords[i][1] = y[i];
        }
        return new Points(coords, color);
    }

    @Override
    public void draw(Painter painter) {
        if (coords == null) {
            return;
        }
        for (double[] coord : coords) {
            if (coord != null && coord.length >= 2) {
                painter.getPoint(coord[0], coord[1]).ifPresent(p ->
                    painter.paint(p.x(), p.y(), color));
            }
        }
    }
}
