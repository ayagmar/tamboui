/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.toolkit.event;

import ink.glimt.tui.event.KeyEvent;

/**
 * Handler for key events on an element.
 */
@FunctionalInterface
public interface KeyEventHandler {
    /**
     * Handles a key event.
     *
     * @param event the key event
     * @return HANDLED if the event was handled and should not propagate, UNHANDLED otherwise
     */
    EventResult handle(KeyEvent event);
}
