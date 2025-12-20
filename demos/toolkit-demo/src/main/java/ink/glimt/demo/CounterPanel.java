/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.element.Element;
import ink.glimt.toolkit.event.EventResult;
import ink.glimt.style.Color;
import ink.glimt.tui.Keys;
import ink.glimt.tui.event.KeyEvent;

import static ink.glimt.toolkit.Toolkit.*;

/**
 * A panel with an incrementable/decrementable counter.
 */
final class CounterPanel extends PanelContent {
    private int counter = 0;

    CounterPanel() {
        super("[Counter]", 24, 5, Color.GREEN);
    }

    @Override
    Element render(boolean focused) {
        var display = String.format("%+d", counter);
        var valueColor = counter > 0 ? Color.GREEN : (counter < 0 ? Color.RED : Color.WHITE);
        return column(
            row(text("Value: ").dim(), text(display).bold().fg(valueColor)),
            text("[↑/k] Inc  [↓/j] Dec").dim()
        );
    }

    @Override
    EventResult handleKey(KeyEvent event) {
        if (Keys.isUp(event) || Keys.isChar(event, 'k') || Keys.isChar(event, 'K')) {
            counter++;
            return EventResult.HANDLED;
        }
        if (Keys.isDown(event) || Keys.isChar(event, 'j') || Keys.isChar(event, 'J')) {
            counter--;
            return EventResult.HANDLED;
        }
        return EventResult.UNHANDLED;
    }
}
