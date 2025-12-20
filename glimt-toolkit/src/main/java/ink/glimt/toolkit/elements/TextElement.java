/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.toolkit.elements;

import ink.glimt.toolkit.element.RenderContext;
import ink.glimt.toolkit.element.StyledElement;
import ink.glimt.layout.Rect;
import ink.glimt.terminal.Frame;
import ink.glimt.text.Line;
import ink.glimt.text.Span;
import ink.glimt.widgets.paragraph.Paragraph;

/**
 * A simple text element that displays styled text.
 */
public final class TextElement extends StyledElement<TextElement> {

    private final String content;

    public TextElement(String content) {
        this.content = content != null ? content : "";
    }

    public TextElement(Object value) {
        this.content = value != null ? String.valueOf(value) : "";
    }

    /**
     * Returns the text content.
     */
    public String content() {
        return content;
    }

    @Override
    public void render(Frame frame, Rect area, RenderContext context) {
        if (area.isEmpty() || content.isEmpty()) {
            return;
        }

        // Create a styled span and render as a paragraph
        Span span = Span.styled(content, style);
        Paragraph paragraph = Paragraph.from(Line.from(span));
        frame.renderWidget(paragraph, area);
    }
}
