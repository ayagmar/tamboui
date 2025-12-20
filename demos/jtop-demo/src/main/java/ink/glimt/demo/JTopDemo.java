/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.app.ToolkitRunner;
import ink.glimt.style.Color;
import ink.glimt.tui.TuiConfig;

import java.time.Duration;

import static ink.glimt.toolkit.Toolkit.*;

/**
 * JTop - A "top" alternative built with Glimt DSL.
 * <p>
 * Displays system metrics including:
 * <ul>
 *   <li>CPU usage with history sparkline</li>
 *   <li>Memory usage with gauge</li>
 *   <li>Top processes by CPU/memory</li>
 *   <li>System information</li>
 * </ul>
 */
public class JTopDemo {

    public static void main(String[] args) throws Exception {
        new JTopDemo().run();
    }

    public void run() throws Exception {
        var config = TuiConfig.builder()
            .tickRate(Duration.ofMillis(500))
            .build();

        // Create stateful component outside the render supplier
        var systemMonitor = new SystemMonitor();

        try (var runner = ToolkitRunner.create(config)) {
            runner.run(() -> column(
                panel(() -> row(
                    text(" JTop - System Monitor ").bold().cyan(),
                    spacer(),
                    text(" [s] Sort ").dim(),
                    text(" [q] Quit ").dim()
                )).rounded().borderColor(Color.DARK_GRAY).length(3),
                systemMonitor
            ));
        }
    }
}
