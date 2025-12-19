/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.canvas.shapes;

import io.github.jratatui.style.Color;
import io.github.jratatui.widgets.canvas.Painter;
import io.github.jratatui.widgets.canvas.Shape;

/**
 * A rectangle defined by position and size.
 * <p>
 * The rectangle is positioned from its bottom-left corner in
 * canvas coordinate space (mathematical coordinates).
 *
 * <pre>{@code
 * // Rectangle at (10, 20) with width 30 and height 15
 * context.draw(new Rectangle(10, 20, 30, 15, Color.BLUE));
 * }</pre>
 *
 * @see Shape
 */
public record Rectangle(double x, double y, double width, double height, Color color) implements Shape {

    /**
     * Creates a rectangle at (x, y) with the given dimensions and color.
     */
    public static Rectangle of(double x, double y, double width, double height, Color color) {
        return new Rectangle(x, y, width, height, color);
    }

    @Override
    public void draw(Painter painter) {
        // Draw four edges using lines
        double x1 = x;
        double y1 = y;
        double x2 = x + width;
        double y2 = y + height;

        // Bottom edge
        drawLine(painter, x1, y1, x2, y1);
        // Top edge
        drawLine(painter, x1, y2, x2, y2);
        // Left edge
        drawLine(painter, x1, y1, x1, y2);
        // Right edge
        drawLine(painter, x2, y1, x2, y2);
    }

    private void drawLine(Painter painter, double x1, double y1, double x2, double y2) {
        var p1 = painter.getPoint(x1, y1);
        var p2 = painter.getPoint(x2, y2);

        if (p1.isPresent() && p2.isPresent()) {
            drawBresenham(painter, p1.get().x(), p1.get().y(), p2.get().x(), p2.get().y());
        } else {
            // Sample along line for partial visibility
            double dx = x2 - x1;
            double dy = y2 - y1;
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) {
                painter.getPoint(x1, y1).ifPresent(p ->
                    painter.paint(p.x(), p.y(), color));
                return;
            }
            int steps = Math.max(1, (int) Math.ceil(length / 0.5));
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                painter.getPoint(x1 + t * dx, y1 + t * dy).ifPresent(p ->
                    painter.paint(p.x(), p.y(), color));
            }
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
