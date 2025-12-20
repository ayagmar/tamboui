/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.toolkit.app.ToolkitRunner;
import ink.glimt.toolkit.element.Element;
import ink.glimt.style.Color;
import ink.glimt.tui.TuiConfig;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static ink.glimt.toolkit.Toolkit.*;

/**
 * Widget Playground Demo showcasing the Glimt DSL.
 * <p>
 * Features demonstrated:
 * <ul>
 *   <li>Lambda-based DSL for dynamic content</li>
 *   <li>Stateful components (TODO list with input field)</li>
 *   <li>Event handlers on elements</li>
 *   <li>Draggable floating panels</li>
 *   <li>Focus navigation (Tab/Shift+Tab)</li>
 *   <li>Dynamic component creation</li>
 * </ul>
 */
public class ToolkitDemo {

    public ToolkitDemo() {
    }

    public static void main(String[] args) throws Exception {
        new ToolkitDemo().run();
    }

    public void run() throws Exception {
        var config = TuiConfig.builder()
            .mouseCapture(true)
            .tickRate(Duration.ofMillis(100))
            .build();

        try (var runner = ToolkitRunner.create(config)) {
            var panels = new FloatingPanelsArea();
            runner.run(() -> column(
                    panel(() -> row(
                            text(" Glimt Widget Playground ").bold().cyan(),
                            spacer(),
                            text(" [1-6] Add Panel ").dim(),
                            text(" [Tab] Focus ").dim(),
                            text(" [Drag] Move ").dim(),
                            text(" [x] Delete ").dim(),
                            text(" [q] Quit ").dim()
                    )).rounded().borderColor(Color.DARK_GRAY).length(3),
                    panels
            ));
        }
    }

}
