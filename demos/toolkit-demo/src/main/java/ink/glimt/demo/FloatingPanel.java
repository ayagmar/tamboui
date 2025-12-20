/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

/**
 * Container for a floating panel with position and content.
 */
final class FloatingPanel {
    final int id;
    final PanelContent content;
    int x, y;

    FloatingPanel(int id, PanelContent content, int x, int y) {
        this.id = id;
        this.content = content;
        this.x = x;
        this.y = y;
    }

    String panelId() {
        return content.getClass().getSimpleName().toLowerCase() + "-" + id;
    }
}
