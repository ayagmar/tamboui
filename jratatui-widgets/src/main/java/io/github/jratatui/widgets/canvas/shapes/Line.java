/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.canvas.shapes;

import io.github.jratatui.style.Color;
import io.github.jratatui.widgets.canvas.Painter;
import io.github.jratatui.widgets.canvas.Shape;

/**
 * A line segment between two points.
 * <p>
 * Draws a line from (x1, y1) to (x2, y2) using Bresenham's algorithm.
 *
 * <pre>{@code
 * context.draw(new Line(0, 0, 10, 10, Color.RED));
 * }</pre>
 *
 * @see Shape
 */
public record Line(double x1, double y1, double x2, double y2, Color color) implements Shape {

    /**
     * Creates a line from (x1, y1) to (x2, y2) with the given color.
     */
    public static Line of(double x1, double y1, double x2, double y2, Color color) {
        return new Line(x1, y1, x2, y2, color);
    }

    @Override
    public void draw(Painter painter) {
        var p1 = painter.getPoint(x1, y1);
        var p2 = painter.getPoint(x2, y2);

        if (p1.isEmpty() || p2.isEmpty()) {
            // Fall back to drawing what we can if points are partially visible
            drawWithClipping(painter);
            return;
        }

        drawBresenham(painter, p1.get().x(), p1.get().y(), p2.get().x(), p2.get().y());
    }

    private void drawWithClipping(Painter painter) {
        // Sample points along the line and draw visible ones
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            painter.getPoint(x1, y1).ifPresent(p ->
                painter.paint(p.x(), p.y(), color));
            return;
        }

        // Step size - smaller for longer lines
        double step = 0.5;
        int steps = (int) Math.ceil(length / step);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = x1 + t * dx;
            double y = y1 + t * dy;
            painter.getPoint(x, y).ifPresent(p ->
                painter.paint(p.x(), p.y(), color));
        }
    }

    private void drawBresenham(Painter painter, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            painter.paint(x0, y0, color);

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}
