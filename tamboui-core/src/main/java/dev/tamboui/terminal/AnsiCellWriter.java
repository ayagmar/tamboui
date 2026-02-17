/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.terminal;

import java.util.Objects;
import java.util.function.Consumer;

import dev.tamboui.buffer.Cell;
import dev.tamboui.style.Hyperlink;
import dev.tamboui.style.Style;

/**
 * Stateful cell-to-ANSI renderer that converts a sequence of {@link Cell} values
 * into ANSI escape sequences and delivers them to a provided output sink.
 *
 * <p>This class tracks the last emitted {@link Style} across successive
 * {@link #writeCell(Cell)} calls, suppressing redundant escape sequences when the
 * style has not changed. Style extensions such as hyperlinks are handled transparently.
 *
 * <p>Closing the writer flushes any pending state and emits the ANSI reset sequence.
 * Use try-with-resources to ensure proper cleanup:
 *
 * <pre>{@code
 * StringBuilder sb = new StringBuilder();
 * try (AnsiCellWriter writer = new AnsiCellWriter(sb::append)) {
 *     for (int x = 0; x < width; x++) {
 *         writer.writeCell(buffer.get(x, y));
 *     }
 * }
 * String ansi = sb.toString();
 * }</pre>
 *
 * <p>Instances are not thread-safe. Create a new instance per rendering pass.
 *
 * @see AnsiStringBuilder
 */
public final class AnsiCellWriter implements AutoCloseable {

    private final Consumer<String> sink;
    private Style lastStyle;
    private Hyperlink lastHyperlink;

    /**
     * Creates a new writer that sends ANSI output to the given sink.
     *
     * @param sink the output consumer
     */
    public AnsiCellWriter(Consumer<String> sink) {
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    /**
     * Writes a single cell to the output sink.
     *
     * <p>Continuation cells (wide-character placeholders) are silently skipped.
     * When the cell's style differs from the previously written cell, the
     * appropriate escape sequences are emitted.
     *
     * @param cell the cell to write
     */
    public void writeCell(Cell cell) {
        if (cell.isContinuation()) {
            return;
        }

        Style style = cell.style();
        if (!style.equals(lastStyle)) {
            Hyperlink currentHyperlink = style.hyperlink().orElse(null);
            if (!Objects.equals(currentHyperlink, lastHyperlink)) {
                if (lastHyperlink != null) {
                    sink.accept(AnsiStringBuilder.hyperlinkEnd());
                }
                if (currentHyperlink != null) {
                    sink.accept(AnsiStringBuilder.hyperlinkStart(currentHyperlink));
                }
                lastHyperlink = currentHyperlink;
            }
            sink.accept(AnsiStringBuilder.styleToAnsi(style));
            lastStyle = style;
        }

        sink.accept(cell.symbol());
    }

    /**
     * Closes this writer by flushing any pending state and emitting
     * the ANSI reset sequence ({@link AnsiStringBuilder#RESET}).
     */
    @Override
    public void close() {
        if (lastHyperlink != null) {
            sink.accept(AnsiStringBuilder.hyperlinkEnd());
        }
        sink.accept(AnsiStringBuilder.RESET);
    }
}
