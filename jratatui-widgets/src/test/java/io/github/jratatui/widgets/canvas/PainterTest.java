/*
 * Copyright (c) 2025 JRatatui Contributors
 * SPDX-License-Identifier: MIT
 */
package io.github.jratatui.widgets.canvas;

import io.github.jratatui.style.Color;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PainterTest {

    @Test
    void getPoint_returns_grid_coordinates() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(0, 0);
        assertThat(point).isPresent();
        assertThat(point.get().x()).isEqualTo(0);
        assertThat(point.get().y()).isEqualTo(9);  // Flipped (bottom-left origin)
    }

    @Test
    void getPoint_top_left() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(0, 10);
        assertThat(point).isPresent();
        assertThat(point.get().x()).isEqualTo(0);
        assertThat(point.get().y()).isEqualTo(0);
    }

    @Test
    void getPoint_bottom_right() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(10, 0);
        assertThat(point).isPresent();
        assertThat(point.get().x()).isEqualTo(9);
        assertThat(point.get().y()).isEqualTo(9);
    }

    @Test
    void getPoint_center() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(5, 5);
        assertThat(point).isPresent();
        // Center of grid
    }

    @Test
    void getPoint_outside_left() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(-1, 5);
        assertThat(point).isEmpty();
    }

    @Test
    void getPoint_outside_right() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(11, 5);
        assertThat(point).isEmpty();
    }

    @Test
    void getPoint_outside_top() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(5, 11);
        assertThat(point).isEmpty();
    }

    @Test
    void getPoint_outside_bottom() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(5, -1);
        assertThat(point).isEmpty();
    }

    @Test
    void getPoint_with_negative_bounds() {
        var ctx = new Context(20, 20, new double[] {-10, 10}, new double[] {-10, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        var origin = painter.getPoint(0, 0);
        assertThat(origin).isPresent();
        // Should map to center of grid
        assertThat(origin.get().x()).isEqualTo(10);
        assertThat(origin.get().y()).isEqualTo(10);
    }

    @Test
    void paint_sets_color() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        painter.paint(5, 5, Color.RED);

        var layers = ctx.allLayers();
        assertThat(layers.get(0)[5][5]).isEqualTo(Color.RED);
    }

    @Test
    void paint_outside_grid_is_ignored() {
        var ctx = new Context(10, 10, new double[] {0, 10}, new double[] {0, 10}, Marker.DOT);
        var painter = new Painter(ctx);

        painter.paint(-1, -1, Color.RED);
        painter.paint(100, 100, Color.RED);
        // Should not throw
    }

    @Test
    void getPoint_zero_range_returns_empty() {
        var ctx = new Context(10, 10, new double[] {5, 5}, new double[] {5, 5}, Marker.DOT);
        var painter = new Painter(ctx);

        var point = painter.getPoint(5, 5);
        assertThat(point).isEmpty();
    }
}
