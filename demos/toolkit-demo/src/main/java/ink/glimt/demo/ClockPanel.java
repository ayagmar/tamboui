/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.element.Element;
import ink.glimt.style.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static ink.glimt.toolkit.Toolkit.*;

/**
 * A panel displaying the current time and date.
 */
final class ClockPanel extends PanelContent {

    ClockPanel() {
        super("[Clock]", 22, 4, Color.CYAN);
    }

    @Override
    Element render(boolean focused) {
        var time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d"));
        return column(
            text(time).bold().cyan(),
            text(date).dim()
        );
    }
}
