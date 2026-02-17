/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.terminal;

import java.io.IOException;

import dev.tamboui.buffer.Cell;
import dev.tamboui.buffer.CellUpdate;
import dev.tamboui.error.RuntimeIOException;
import dev.tamboui.layout.Position;

/**
 * Base class for terminal backends that produce ANSI output.
 * <p>
 * Provides final implementations of {@link #draw(Iterable)} and
 * {@link #setCursorPosition(Position)} so that all concrete backends
 * share a single, consistent rendering path through {@link AnsiCellWriter}.
 * <p>
 * Subclasses must implement the raw I/O primitives ({@link #writeRaw(String)},
 * {@link #flush()}, etc.) but cannot override the drawing or cursor-positioning
 * logic.
 *
 * @see AnsiCellWriter
 */
public abstract class AbstractBackend implements Backend {

    /**
     * Creates a new abstract backend.
     */
    protected AbstractBackend() {
    }

    /**
     * Draws the given cell updates to the terminal.
     * <p>
     * Iterates over the updates, positions the cursor for each cell,
     * and writes styled content using {@link AnsiCellWriter}.
     * Output is sent via {@link #writeRaw(String)}.
     *
     * @param updates the cell updates to draw
     * @throws IOException if drawing fails
     */
    @Override
    public final void draw(Iterable<CellUpdate> updates) throws IOException {
        try (AnsiCellWriter cellWriter = new AnsiCellWriter(s -> {
            try {
                writeRaw(s);
            } catch (IOException e) {
                throw new RuntimeIOException("Failed to write cell data", e);
            }
        })) {
            for (CellUpdate update : updates) {
                Cell cell = update.cell();
                if (cell.isContinuation()) {
                    continue;
                }
                // ANSI uses 1-based coordinates
                writeRaw("\u001b[" + (update.y() + 1) + ";" + (update.x() + 1) + "H");
                cellWriter.writeCell(cell);
            }
        }
    }

    /**
     * Sets the cursor to the given position and flushes.
     *
     * @param position the position to set the cursor to
     * @throws IOException if the operation fails
     */
    @Override
    public final void setCursorPosition(Position position) throws IOException {
        // ANSI uses 1-based coordinates
        writeRaw("\u001b[" + (position.y() + 1) + ";" + (position.x() + 1) + "H");
        flush();
    }
}
