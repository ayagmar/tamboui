/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.toolkit.app;

import ink.glimt.toolkit.component.ComponentTree;
import ink.glimt.toolkit.element.Element;
import ink.glimt.toolkit.element.RenderContext;
import ink.glimt.toolkit.event.EventResult;
import ink.glimt.toolkit.event.EventRouter;
import ink.glimt.toolkit.focus.FocusManager;
import ink.glimt.tui.Keys;
import ink.glimt.tui.TuiConfig;
import ink.glimt.tui.TuiRunner;
import ink.glimt.tui.event.Event;
import ink.glimt.tui.event.TickEvent;

import java.util.function.Supplier;

/**
 * Runner for DSL applications with automatic event routing.
 * <p>
 * Events are routed to elements based on focus and position.
 * Elements handle their own events via handlers set with {@code onKeyEvent()}
 * and {@code onMouseEvent()}.
 *
 * <pre>{@code
 * import static ink.glimt.toolkit.Toolkit.*;
 *
 * try (var runner = ToolkitRunner.create()) {
 *     int[] count = {0};  // State in lambda scope
 *
 *     runner.run(() ->
 *         panel("Counter",
 *             text("Count: " + count[0]).bold().cyan()
 *         )
 *         .rounded()
 *         .id("counter")
 *         .focusable()
 *         .onKeyEvent(event -> {
 *             if (Keys.isUp(event)) {
 *                 count[0]++;
 *                 return EventResult.HANDLED;
 *             }
 *             return EventResult.UNHANDLED;
 *         })
 *     );
 * }
 * }</pre>
 */
public final class ToolkitRunner implements AutoCloseable {

    private final TuiRunner tuiRunner;
    private final FocusManager focusManager;
    private final ComponentTree componentTree;
    private final EventRouter eventRouter;
    private final RenderContext renderContext;

    private ToolkitRunner(TuiRunner tuiRunner) {
        this.tuiRunner = tuiRunner;
        this.focusManager = new FocusManager();
        this.componentTree = new ComponentTree();
        this.eventRouter = new EventRouter(focusManager);
        this.renderContext = new RenderContext(focusManager, componentTree, eventRouter);
    }

    /**
     * Creates a ToolkitRunner with default configuration.
     *
     * @return a new ToolkitRunner
     * @throws Exception if terminal initialization fails
     */
    public static ToolkitRunner create() throws Exception {
        return create(TuiConfig.defaults());
    }

    /**
     * Creates a ToolkitRunner with the specified configuration.
     *
     * @param config the configuration to use
     * @return a new ToolkitRunner
     * @throws Exception if terminal initialization fails
     */
    public static ToolkitRunner create(TuiConfig config) throws Exception {
        TuiRunner tuiRunner = TuiRunner.create(config);
        return new ToolkitRunner(tuiRunner);
    }

    /**
     * Runs the application with the given element supplier.
     * The supplier is called each frame to get the current UI state.
     * <p>
     * Events are routed to elements based on their handlers.
     * Press 'q' or Ctrl+C to quit.
     *
     * @param elementSupplier provides the root element for each render
     * @throws Exception if an error occurs during execution
     */
    public void run(Supplier<Element> elementSupplier) throws Exception {
        tuiRunner.run(
            (event, runner) -> handleEvent(event),
            frame -> {
                // Clear state before each render
                focusManager.clearFocusables();
                componentTree.clear();
                eventRouter.clear();

                // Get the current element tree
                Element root = elementSupplier.get();

                // Render the element tree
                // Elements register themselves during render if they have handlers
                if (root != null) {
                    root.render(frame, frame.area(), renderContext);
                }
            }
        );
    }

    private boolean handleEvent(Event event) {
        // Handle quit
        if (Keys.isQuit(event)) {
            quit();
            return false;
        }

        // Tick events always trigger a redraw for animations
        if (event instanceof TickEvent) {
            return true;
        }

        // Route to elements - they handle their own events
        EventResult result = eventRouter.route(event);

        return result.isHandled();
    }

    /**
     * Signals the runner to stop.
     */
    public void quit() {
        tuiRunner.quit();
    }

    /**
     * Returns whether the runner is still running.
     */
    public boolean isRunning() {
        return tuiRunner.isRunning();
    }

    /**
     * Returns the focus manager.
     */
    public FocusManager focusManager() {
        return focusManager;
    }

    /**
     * Returns the event router.
     */
    public EventRouter eventRouter() {
        return eventRouter;
    }

    /**
     * Returns the underlying TuiRunner.
     */
    public TuiRunner tuiRunner() {
        return tuiRunner;
    }

    @Override
    public void close() {
        tuiRunner.close();
    }
}
