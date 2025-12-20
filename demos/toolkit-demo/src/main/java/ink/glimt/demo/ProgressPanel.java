/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.element.Element;
import ink.glimt.style.Color;

import static ink.glimt.toolkit.Toolkit.*;

/**
 * A panel with an auto-animating progress bar.
 */
final class ProgressPanel extends PanelContent {
    private int progressValue = 0;
    private boolean progressDirection = true;

    ProgressPanel() {
        super("[Progress]", 28, 5, Color.BLUE);
    }

    @Override
    void onTick(long tick) {
        if (tick % 3 == 0) {
            if (progressDirection) {
                progressValue++;
                if (progressValue >= 100) progressDirection = false;
            } else {
                progressValue--;
                if (progressValue <= 0) progressDirection = true;
            }
        }
    }

    @Override
    Element render(boolean focused) {
        var barColor = progressValue < 30 ? Color.RED :
                      (progressValue < 70 ? Color.YELLOW : Color.GREEN);
        return column(
            lineGauge(progressValue)
                .label("Progress: ")
                .filledColor(barColor)
                .thick(),
            text("Auto-animating...").dim()
        );
    }
}
