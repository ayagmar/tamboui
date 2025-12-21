/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.demo;

import ink.glimt.layout.Constraint;
import ink.glimt.layout.Layout;
import ink.glimt.layout.Rect;
import ink.glimt.style.Color;
import ink.glimt.style.Style;
import ink.glimt.terminal.Backend;
import ink.glimt.terminal.BackendFactory;
import ink.glimt.terminal.Frame;
import ink.glimt.terminal.Terminal;
import ink.glimt.text.Line;
import ink.glimt.text.Span;
import ink.glimt.text.Text;
import ink.glimt.widgets.block.Block;
import ink.glimt.widgets.block.BorderType;
import ink.glimt.widgets.block.Borders;
import ink.glimt.widgets.block.Title;
import ink.glimt.widgets.list.ListItem;
import ink.glimt.widgets.list.ListState;
import ink.glimt.widgets.list.ListWidget;
import ink.glimt.widgets.paragraph.Paragraph;
import ink.glimt.widgets.scrollbar.Scrollbar;
import ink.glimt.widgets.scrollbar.ScrollbarOrientation;
import ink.glimt.widgets.scrollbar.ScrollbarState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo TUI application showcasing the Scrollbar widget.
 * <p>
 * Demonstrates scrollbars with different orientations, styles,
 * and integration with a scrollable list.
 */
public class ScrollbarDemo {

    private static final int ITEM_COUNT = 100;

    private boolean running = true;
    private final List<String> items = new ArrayList<>();
    private final ListState listState = new ListState();
    private final ScrollbarState verticalScrollState = new ScrollbarState(ITEM_COUNT);
    private final ScrollbarState horizontalScrollState = new ScrollbarState(50);

    public static void main(String[] args) throws Exception {
        new ScrollbarDemo().run();
    }

    public ScrollbarDemo() {
        // Generate sample items
        for (int i = 1; i <= ITEM_COUNT; i++) {
            items.add("Item " + i + " - This is a sample list item with some extra text for horizontal scrolling");
        }
        listState.select(0);
        verticalScrollState.viewportContentLength(10);
    }

    public void run() throws Exception {
        try (Backend backend = BackendFactory.create()) {
            backend.enableRawMode();
            backend.enterAlternateScreen();
            backend.hideCursor();

            Terminal<Backend> terminal = new Terminal<>(backend);

            // Handle resize
            backend.onResize(() -> {
                try {
                    terminal.draw(this::ui);
                } catch (IOException e) {
                    // Ignore
                }
            });

            // Initial draw
            terminal.draw(this::ui);

            // Event loop
            while (running) {
                int c = backend.read(100);
                if (c == -2 || c == -1) {
                    continue;
                }

                boolean needsRedraw = handleInput(c, backend);
                if (needsRedraw) {
                    terminal.draw(this::ui);
                }
            }
        }
    }

    private boolean handleInput(int c, Backend backend) throws IOException {
        // Handle escape sequences
        if (c == 27) {
            int next = backend.peek(50);
            if (next == '[') {
                backend.read(50);
                int code = backend.read(50);
                return handleEscapeSequence(code);
            }
            return false;
        }

        return switch (c) {
            case 'q', 'Q', 3 -> {
                running = false;
                yield true;
            }
            case 'j' -> {
                selectNext();
                yield true;
            }
            case 'k' -> {
                selectPrevious();
                yield true;
            }
            case 'g' -> {
                selectFirst();
                yield true;
            }
            case 'G' -> {
                selectLast();
                yield true;
            }
            case 'h' -> {
                horizontalScrollState.prev();
                yield true;
            }
            case 'l' -> {
                horizontalScrollState.next();
                yield true;
            }
            case ' ' -> { // Page down
                pageDown();
                yield true;
            }
            default -> false;
        };
    }

    private boolean handleEscapeSequence(int code) {
        return switch (code) {
            case 'A' -> { // Up arrow
                selectPrevious();
                yield true;
            }
            case 'B' -> { // Down arrow
                selectNext();
                yield true;
            }
            case 'C' -> { // Right arrow
                horizontalScrollState.next();
                yield true;
            }
            case 'D' -> { // Left arrow
                horizontalScrollState.prev();
                yield true;
            }
            case '5' -> { // Page Up (ESC [ 5 ~)
                pageUp();
                yield true;
            }
            case '6' -> { // Page Down (ESC [ 6 ~)
                pageDown();
                yield true;
            }
            default -> false;
        };
    }

    private void selectNext() {
        Integer current = listState.selected();
        if (current == null) {
            listState.select(0);
        } else if (current < items.size() - 1) {
            listState.select(current + 1);
        }
        syncScrollState();
    }

    private void selectPrevious() {
        Integer current = listState.selected();
        if (current == null) {
            listState.select(0);
        } else if (current > 0) {
            listState.select(current - 1);
        }
        syncScrollState();
    }

    private void selectFirst() {
        listState.select(0);
        syncScrollState();
    }

    private void selectLast() {
        listState.select(items.size() - 1);
        syncScrollState();
    }

    private void pageUp() {
        Integer current = listState.selected();
        if (current == null) {
            listState.select(0);
        } else {
            listState.select(Math.max(0, current - 10));
        }
        syncScrollState();
    }

    private void pageDown() {
        Integer current = listState.selected();
        if (current == null) {
            listState.select(0);
        } else {
            listState.select(Math.min(items.size() - 1, current + 10));
        }
        syncScrollState();
    }

    private void syncScrollState() {
        Integer selected = listState.selected();
        if (selected != null) {
            verticalScrollState.position(selected);
        }
    }

    private void ui(Frame frame) {
        Rect area = frame.area();

        List<Rect> layout = Layout.vertical()
            .constraints(
                Constraint.length(3),  // Header
                Constraint.fill(),     // Main content
                Constraint.length(3)   // Footer
            )
            .split(area);

        renderHeader(frame, layout.get(0));
        renderMainContent(frame, layout.get(1));
        renderFooter(frame, layout.get(2));
    }

    private void renderHeader(Frame frame, Rect area) {
        Block headerBlock = Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.CYAN))
            .title(Title.from(
                Line.from(
                    Span.raw(" Glimt ").bold().cyan(),
                    Span.raw("Scrollbar Demo ").yellow()
                )
            ).centered())
            .build();

        frame.renderWidget(headerBlock, area);
    }

    private void renderMainContent(Frame frame, Rect area) {
        // Split into two columns
        List<Rect> columns = Layout.horizontal()
            .constraints(
                Constraint.percentage(50),
                Constraint.percentage(50)
            )
            .split(area);

        renderListWithScrollbar(frame, columns.get(0));
        renderScrollbarShowcase(frame, columns.get(1));
    }

    private void renderListWithScrollbar(Frame frame, Rect area) {
        Block block = Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.GREEN))
            .title(Title.from(Line.from(
                Span.raw(" List with Scrollbar ").green()
            )))
            .build();

        Rect innerArea = block.inner(area);
        frame.renderWidget(block, area);

        // Reserve space for scrollbar (1 column on right)
        Rect listArea = new Rect(innerArea.x(), innerArea.y(),
            innerArea.width() - 1, innerArea.height());
        Rect scrollbarArea = new Rect(innerArea.right() - 1, innerArea.y(),
            1, innerArea.height());

        // Create list items
        List<ListItem> listItems = items.stream()
            .map(item -> ListItem.from(Text.from(item)))
            .toList();

        ListWidget list = ListWidget.builder()
            .items(listItems)
            .highlightStyle(Style.EMPTY.fg(Color.YELLOW).bold())
            .highlightSymbol("> ")
            .build();

        frame.renderStatefulWidget(list, listArea, listState);

        // Update viewport content length based on actual visible area
        verticalScrollState.viewportContentLength(listArea.height());

        // Render scrollbar
        Scrollbar scrollbar = Scrollbar.builder()
            .orientation(ScrollbarOrientation.VERTICAL_RIGHT)
            .thumbStyle(Style.EMPTY.fg(Color.YELLOW))
            .trackStyle(Style.EMPTY.fg(Color.DARK_GRAY))
            .beginSymbol("▲")
            .endSymbol("▼")
            .build();

        frame.renderStatefulWidget(scrollbar, scrollbarArea, verticalScrollState);
    }

    private void renderScrollbarShowcase(Frame frame, Rect area) {
        Block block = Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.MAGENTA))
            .title(Title.from(Line.from(
                Span.raw(" Scrollbar Styles ").magenta()
            )))
            .build();

        Rect innerArea = block.inner(area);
        frame.renderWidget(block, area);

        // Split into rows for different scrollbar examples
        List<Rect> rows = Layout.vertical()
            .constraints(
                Constraint.length(3),  // Description
                Constraint.length(3),  // Horizontal scrollbar example
                Constraint.fill(),     // Vertical scrollbar showcase
                Constraint.length(1)   // Spacer
            )
            .split(innerArea);

        // Description
        Paragraph desc = Paragraph.builder()
            .text(Text.from(Line.from(
                Span.raw("Use "),
                Span.raw("h/l").bold().yellow(),
                Span.raw(" or "),
                Span.raw("←/→").bold().yellow(),
                Span.raw(" to scroll horizontally")
            )))
            .build();
        frame.renderWidget(desc, rows.get(0));

        // Horizontal scrollbar
        renderHorizontalExample(frame, rows.get(1));

        // Vertical scrollbar styles showcase
        renderVerticalStylesShowcase(frame, rows.get(2));
    }

    private void renderHorizontalExample(Frame frame, Rect area) {
        Block block = Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.BLUE))
            .build();

        Rect innerArea = block.inner(area);
        frame.renderWidget(block, area);

        // Reserve bottom row for scrollbar
        Rect contentArea = new Rect(innerArea.x(), innerArea.y(),
            innerArea.width(), innerArea.height() - 1);
        Rect scrollbarArea = new Rect(innerArea.x(), innerArea.bottom() - 1,
            innerArea.width(), 1);

        // Content with position indicator
        String content = String.format("Horizontal Position: %d/%d",
            horizontalScrollState.position() + 1, horizontalScrollState.contentLength());
        Paragraph para = Paragraph.builder()
            .text(Text.from(content))
            .build();
        frame.renderWidget(para, contentArea);

        // Horizontal scrollbar
        Scrollbar scrollbar = Scrollbar.builder()
            .orientation(ScrollbarOrientation.HORIZONTAL_BOTTOM)
            .symbols(Scrollbar.SymbolSet.HORIZONTAL)
            .thumbStyle(Style.EMPTY.fg(Color.CYAN))
            .trackStyle(Style.EMPTY.fg(Color.DARK_GRAY))
            .build();

        frame.renderStatefulWidget(scrollbar, scrollbarArea, horizontalScrollState);
    }

    private void renderVerticalStylesShowcase(Frame frame, Rect area) {
        // Split into columns for different styles
        List<Rect> columns = Layout.horizontal()
            .constraints(
                Constraint.ratio(1, 4),
                Constraint.ratio(1, 4),
                Constraint.ratio(1, 4),
                Constraint.ratio(1, 4)
            )
            .split(area);

        // Style 1: Default
        renderScrollbarStyle(frame, columns.get(0), "Default",
            Scrollbar.builder()
                .orientation(ScrollbarOrientation.VERTICAL_RIGHT)
                .build(),
            Style.EMPTY.fg(Color.WHITE));

        // Style 2: Double line with arrows
        renderScrollbarStyle(frame, columns.get(1), "Double",
            Scrollbar.builder()
                .orientation(ScrollbarOrientation.VERTICAL_RIGHT)
                .symbols(Scrollbar.SymbolSet.DOUBLE_VERTICAL)
                .thumbStyle(Style.EMPTY.fg(Color.GREEN))
                .build(),
            Style.EMPTY.fg(Color.GREEN));

        // Style 3: Custom symbols
        renderScrollbarStyle(frame, columns.get(2), "Custom",
            Scrollbar.builder()
                .orientation(ScrollbarOrientation.VERTICAL_RIGHT)
                .trackSymbol("░")
                .thumbSymbol("▓")
                .thumbStyle(Style.EMPTY.fg(Color.YELLOW))
                .trackStyle(Style.EMPTY.fg(Color.DARK_GRAY))
                .build(),
            Style.EMPTY.fg(Color.YELLOW));

        // Style 4: Minimal (no begin/end)
        renderScrollbarStyle(frame, columns.get(3), "Minimal",
            Scrollbar.builder()
                .orientation(ScrollbarOrientation.VERTICAL_RIGHT)
                .trackSymbol("│")
                .thumbSymbol("█")
                .thumbStyle(Style.EMPTY.fg(Color.RED))
                .build(),
            Style.EMPTY.fg(Color.RED));
    }

    private void renderScrollbarStyle(Frame frame, Rect area, String title,
                                       Scrollbar scrollbar, Style borderStyle) {
        Block block = Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(borderStyle)
            .title(Title.from(Line.from(Span.raw(" " + title + " "))))
            .build();

        Rect innerArea = block.inner(area);
        frame.renderWidget(block, area);

        // Sync with main scroll state for consistent demo
        frame.renderStatefulWidget(scrollbar, innerArea, verticalScrollState);
    }

    private void renderFooter(Frame frame, Rect area) {
        Line helpLine = Line.from(
            Span.raw(" ↑/k").bold().yellow(),
            Span.raw(" Up  ").dim(),
            Span.raw("↓/j").bold().yellow(),
            Span.raw(" Down  ").dim(),
            Span.raw("g").bold().yellow(),
            Span.raw(" First  ").dim(),
            Span.raw("G").bold().yellow(),
            Span.raw(" Last  ").dim(),
            Span.raw("Space").bold().yellow(),
            Span.raw(" Page  ").dim(),
            Span.raw("q").bold().yellow(),
            Span.raw(" Quit").dim()
        );

        Paragraph footer = Paragraph.builder()
            .text(Text.from(helpLine))
            .block(Block.builder()
                .borders(Borders.ALL)
                .borderType(BorderType.ROUNDED)
                .borderStyle(Style.EMPTY.fg(Color.DARK_GRAY))
                .build())
            .build();

        frame.renderWidget(footer, area);
    }
}
