/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.canvas.shapes;

import io.github.jratatui.style.Color;
import io.github.jratatui.widgets.canvas.Painter;
import io.github.jratatui.widgets.canvas.Shape;

/**
 * A circle defined by center and radius.
 *
 * <pre>{@code
 * // Circle centered at (50, 50) with radius 20
 * context.draw(new Circle(50, 50, 20, Color.GREEN));
 * }</pre>
 *
 * @see Shape
 */
public record Circle(double x, double y, double radius, Color color) implements Shape {

    /**
     * Creates a circle centered at (x, y) with the given radius and color.
     */
    public static Circle of(double x, double y, double radius, Color color) {
        return new Circle(x, y, radius, color);
    }

    @Override
    public void draw(Painter painter) {
        if (radius <= 0) {
            return;
        }

        // Draw circle using Midpoint circle algorithm (Bresenham's for circles)
        // Sample points around the circumference
        double step = 0.5 / radius;  // Smaller step for larger circles
        for (double angle = 0; angle < 2 * Math.PI; angle += step) {
            double px = x + radius * Math.cos(angle);
            double py = y + radius * Math.sin(angle);
            painter.getPoint(px, py).ifPresent(p ->
                painter.paint(p.x(), p.y(), color));
        }

        // Ensure we close the circle by drawing the final point
        painter.getPoint(x + radius, y).ifPresent(p ->
            painter.paint(p.x(), p.y(), color));
    }
}
