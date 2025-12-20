/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.element.Element;
import ink.glimt.toolkit.event.EventResult;
import ink.glimt.style.Color;
import ink.glimt.tui.event.KeyEvent;

/**
 * Base class for panel content implementations.
 */
abstract class PanelContent {
    private final String title;
    private final int width;
    private final int height;
    private final Color color;

    protected PanelContent(String title, int width, int height, Color color) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    String title() { return title; }
    int width() { return width; }
    int height() { return height; }
    Color color() { return color; }

    abstract Element render(boolean focused);

    void onTick(long tick) {}

    EventResult handleKey(KeyEvent event) { return EventResult.UNHANDLED; }
}
