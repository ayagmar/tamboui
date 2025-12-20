/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.toolkit.event;

import ink.glimt.tui.event.MouseEvent;

/**
 * Handler for mouse events on an element.
 */
@FunctionalInterface
public interface MouseEventHandler {
    /**
     * Handles a mouse event.
     *
     * @param event the mouse event
     * @return HANDLED if the event was handled and should not propagate, UNHANDLED otherwise
     */
    EventResult handle(MouseEvent event);
}
