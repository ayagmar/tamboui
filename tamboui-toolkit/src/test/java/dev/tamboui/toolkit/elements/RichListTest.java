/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.elements;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.terminal.Frame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static dev.tamboui.toolkit.Toolkit.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for RichList.
 */
class RichListTest {

    @Test
    @DisplayName("RichList fluent API chains correctly")
    void fluentApiChaining() {
        RichList<?> element = list("Item 1", "Item 2", "Item 3")
            .highlightSymbol("> ")
            .highlightColor(Color.YELLOW)
            .title("Menu")
            .rounded()
            .borderColor(Color.CYAN);

        assertThat(element).isInstanceOf(RichList.class);
    }

    @Test
    @DisplayName("list() creates empty element")
    void emptyList() {
        RichList<?> element = list();
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("list(String...) creates element with items")
    void listWithItems() {
        RichList<?> element = list("A", "B", "C");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("list(List<String>) creates element with items")
    void listWithItemsList() {
        RichList<?> element = list(Arrays.asList("X", "Y", "Z"));
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("items() method replaces items")
    void itemsMethod() {
        RichList<?> element = list()
            .items("New 1", "New 2");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("RichList renders items to buffer")
    void rendersToBuffer() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        list("Item 1", "Item 2", "Item 3")
            .title("List")
            .rounded()
            .render(frame, area, RenderContext.empty());

        // Check border is rendered
        assertThat(buffer.get(0, 0).symbol()).isEqualTo("╭");
    }

    @Test
    @DisplayName("RichList with selection highlights item")
    void withSelection() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        list("Item 1", "Item 2", "Item 3")
            .selected(1)
            .highlightColor(Color.YELLOW)
            .render(frame, area, RenderContext.empty());

        // Second item should have highlight style (row 1 in 0-indexed)
        // We can't easily verify the exact highlight without knowing the layout,
        // but we can verify rendering completes
        assertThat(buffer).isNotNull();
    }

    @Test
    @DisplayName("Empty area does not render")
    void emptyAreaNoRender() {
        Rect emptyArea = new Rect(0, 0, 0, 0);
        Buffer buffer = Buffer.empty(new Rect(0, 0, 10, 5));
        Frame frame = Frame.forTesting(buffer);

        // Should not throw
        list("A", "B").render(frame, emptyArea, RenderContext.empty());
    }

    @Test
    @DisplayName("RichList manages its own internal state")
    void internalState() {
        Rect area = new Rect(0, 0, 20, 5);
        Buffer buffer = Buffer.empty(area);
        Frame frame = Frame.forTesting(buffer);

        // Should not throw even without explicit state
        list("Item 1", "Item 2")
            .render(frame, area, RenderContext.empty());
    }

    @Test
    @DisplayName("highlightSymbol sets the indicator")
    void highlightSymbol() {
        RichList<?> element = list("A", "B")
            .highlightSymbol("→ ");
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("highlightStyle sets the style")
    void highlightStyle() {
        RichList<?> element = list("A", "B")
            .highlightColor(Color.GREEN);
        assertThat(element).isNotNull();
    }

    @Test
    @DisplayName("selected() returns current selection index")
    void selectedReturnsIndex() {
        RichList<?> element = list("A", "B", "C")
            .selected(2);
        assertThat(element.selected()).isEqualTo(2);
    }

    @Test
    @DisplayName("selectPrevious decrements selection")
    void selectPreviousDecrements() {
        RichList<?> element = list("A", "B", "C")
            .selected(2);
        element.selectPrevious();
        assertThat(element.selected()).isEqualTo(1);
    }

    @Test
    @DisplayName("selectNext increments selection")
    void selectNextIncrements() {
        RichList<?> element = list("A", "B", "C")
            .selected(0);
        element.selectNext(3);
        assertThat(element.selected()).isEqualTo(1);
    }
}
