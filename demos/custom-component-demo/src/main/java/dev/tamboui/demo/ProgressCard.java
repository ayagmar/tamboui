/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.demo;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.toolkit.element.StyledElement;
import dev.tamboui.toolkit.elements.GaugeElement;
import dev.tamboui.toolkit.elements.TextElement;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;

/**
 * A custom component demonstrating CSS styling.
 * Displays a task card with title, description, and progress bar.
 */
public final class ProgressCard extends StyledElement<ProgressCard> {

    /**
     * Status of the task shown in the card.
     */
    public enum Status {
        PENDING("pending"),
        IN_PROGRESS("in-progress"),
        COMPLETE("complete");

        private final String cssClass;

        Status(String cssClass) {
            this.cssClass = cssClass;
        }

        public String cssClass() {
            return cssClass;
        }
    }

    private String title = "";
    private String description = "";
    private double progress = 0.0;
    private Status status = Status.PENDING;

    /**
     * Creates a new ProgressCard.
     */
    public ProgressCard() {
    }

    /**
     * Sets the card title.
     */
    public ProgressCard title(String title) {
        this.title = title != null ? title : "";
        return this;
    }

    /**
     * Sets the card description.
     */
    public ProgressCard description(String description) {
        this.description = description != null ? description : "";
        return this;
    }

    /**
     * Sets the progress value (0.0 to 1.0).
     */
    public ProgressCard progress(double progress) {
        this.progress = Math.max(0.0, Math.min(1.0, progress));
        // Auto-update status based on progress
        if (this.progress >= 1.0) {
            this.status = Status.COMPLETE;
        } else if (this.progress > 0.0) {
            this.status = Status.IN_PROGRESS;
        } else {
            this.status = Status.PENDING;
        }
        return this;
    }

    /**
     * Enables keyboard handling for progress adjustment (+/-).
     */
    public ProgressCard enableKeyboardControl() {
        onKeyEvent(event -> {
            if (event.character() == '+' || event.character() == '=') {
                progress(Math.min(1.0, progress + 0.1));
                updateStatusClass();
                return dev.tamboui.toolkit.event.EventResult.HANDLED;
            }
            if (event.character() == '-' || event.character() == '_') {
                progress(Math.max(0.0, progress - 0.1));
                updateStatusClass();
                return dev.tamboui.toolkit.event.EventResult.HANDLED;
            }
            return dev.tamboui.toolkit.event.EventResult.UNHANDLED;
        });
        return this;
    }

    /**
     * Returns the current progress value.
     */
    public double progress() {
        return progress;
    }

    /**
     * Returns the current status.
     */
    public Status status() {
        return status;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    protected void renderContent(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty()) {
            return;
        }

        // Get resolved CSS style
        var cssStyle = context.resolveStyle(this);
        var effectiveStyle = context.currentStyle();

        // Determine border color from CSS or focus state
        var isFocused = elementId != null && context.isFocused(elementId);
        Color borderColor;
        if (isFocused) {
            borderColor = Color.CYAN;
        } else {
            borderColor = cssStyle
                .flatMap(resolved -> resolved.getProperty("border-color"))
                .flatMap(context::parseColor)
                .orElse(Color.GRAY);
        }

        // Get border type from CSS, default to PLAIN
        BorderType borderType = cssStyle
            .flatMap(resolved -> resolved.borderType())
            .orElse(BorderType.PLAIN);

        // Build the card border
        var blockBuilder = Block.builder()
            .borders(Borders.ALL)
            .borderType(borderType)
            .style(effectiveStyle);

        blockBuilder.borderStyle(Style.EMPTY.fg(borderColor));

        var block = blockBuilder.build();
        frame.renderWidget(block, area);

        // Get inner area for content
        var innerArea = block.inner(area);
        if (innerArea.isEmpty()) {
            return;
        }

        // Layout: title (1 line), description (1 line), progress (1 line)
        var layout = Layout.vertical()
            .constraints(
                Constraint.length(1),  // Title
                Constraint.length(1),  // Description
                Constraint.length(1)   // Progress bar
            )
            .split(innerArea);

        // Render title using TextElement with .card-title CSS class
        if (!title.isEmpty() && !layout.isEmpty()) {
            var titleElement = new TextElement(title)
                .addClass("card-title")
                .cssParent(this);  // Set parent for CSS inheritance
            titleElement.render(frame, layout.getFirst(), context);
        }

        // Render description using TextElement with .card-description CSS class
        if (!description.isEmpty() && layout.size() > 1) {
            var descElement = new TextElement(description)
                .addClass("card-description")
                .cssParent(this);
            descElement.render(frame, layout.get(1), context);
        }

        // Render progress bar using GaugeElement with status-based CSS class
        if (layout.size() > 2) {
            var gaugeElement = new GaugeElement()
                .ratio(progress)
                .label(String.format("%.0f%%", progress * 100))
                .addClass("progress-" + status.cssClass())
                .cssParent(this);
            gaugeElement.render(frame, layout.get(2), context);
        }
    }

    /**
     * Updates the CSS classes to include the status class.
     * Call this before rendering if status changes.
     */
    public ProgressCard updateStatusClass() {
        for (var value : Status.values()) {
            removeClass(value.cssClass());
        }
        addClass(status.cssClass());
        return this;
    }
}
