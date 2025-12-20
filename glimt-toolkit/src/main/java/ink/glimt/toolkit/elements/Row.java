/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.toolkit.elements;

import ink.glimt.toolkit.element.Element;
import ink.glimt.toolkit.element.RenderContext;
import ink.glimt.toolkit.element.StyledElement;
import ink.glimt.layout.Constraint;
import ink.glimt.layout.Layout;
import ink.glimt.layout.Rect;
import ink.glimt.terminal.Frame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A horizontal layout container that arranges children in a row.
 */
public final class Row extends StyledElement<Row> {

    private final List<Element> children = new ArrayList<>();
    private int spacing = 0;

    public Row() {
    }

    public Row(Element... children) {
        this.children.addAll(Arrays.asList(children));
    }

    /**
     * Sets the spacing between children.
     */
    public Row spacing(int spacing) {
        this.spacing = Math.max(0, spacing);
        return this;
    }

    /**
     * Adds a child element.
     */
    public Row add(Element child) {
        this.children.add(child);
        return this;
    }

    /**
     * Adds multiple child elements.
     */
    public Row add(Element... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty() || children.isEmpty()) {
            return;
        }

        // Track rendered area for event routing
        setRenderedArea(area);

        // Build constraints, accounting for spacing
        List<Constraint> constraints = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            Constraint c = child.constraint();
            constraints.add(c != null ? c : Constraint.fill());

            // Add spacing constraint between children
            if (spacing > 0 && i < children.size() - 1) {
                constraints.add(Constraint.length(spacing));
            }
        }

        List<Rect> areas = Layout.horizontal()
            .constraints(constraints.toArray(new Constraint[0]))
            .split(area);

        // Render children (skipping spacing areas) and register them for events
        int childIndex = 0;
        for (int i = 0; i < areas.size() && childIndex < children.size(); i++) {
            if (spacing > 0 && i % 2 == 1) {
                // Skip spacing area
                continue;
            }
            Element child = children.get(childIndex);
            Rect childArea = areas.get(i);
            child.render(frame, childArea, context);
            // Always register children - EventRouter will handle event dispatch
            context.registerElement(child, childArea);
            childIndex++;
        }
    }
}
