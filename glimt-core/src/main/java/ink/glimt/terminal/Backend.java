/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.terminal;

import ink.glimt.buffer.CellUpdate;
import ink.glimt.layout.Position;
import ink.glimt.layout.Size;

import java.io.IOException;

/**
 * Backend interface for terminal operations.
 * Implementations handle the actual terminal I/O.
 */
public interface Backend extends AutoCloseable {

    /**
     * Draws the given cell updates to the terminal.
     */
    void draw(Iterable<CellUpdate> updates) throws IOException;

    /**
     * Flushes any buffered output to the terminal.
     */
    void flush() throws IOException;

    /**
     * Clears the terminal screen.
     */
    void clear() throws IOException;

    /**
     * Returns the current terminal size.
     */
    Size size() throws IOException;

    /**
     * Shows the cursor.
     */
    void showCursor() throws IOException;

    /**
     * Hides the cursor.
     */
    void hideCursor() throws IOException;

    /**
     * Gets the current cursor position.
     */
    Position getCursorPosition() throws IOException;

    /**
     * Sets the cursor position.
     */
    void setCursorPosition(Position position) throws IOException;

    /**
     * Enters the alternate screen buffer.
     */
    void enterAlternateScreen() throws IOException;

    /**
     * Leaves the alternate screen buffer.
     */
    void leaveAlternateScreen() throws IOException;

    /**
     * Enables raw mode (disables line buffering and echo).
     */
    void enableRawMode() throws IOException;

    /**
     * Disables raw mode.
     */
    void disableRawMode() throws IOException;

    /**
     * Enables mouse capture.
     */
    default void enableMouseCapture() throws IOException {
        // Optional: not all backends support mouse
    }

    /**
     * Disables mouse capture.
     */
    default void disableMouseCapture() throws IOException {
        // Optional: not all backends support mouse
    }

    /**
     * Scrolls the screen up by the given number of lines.
     */
    default void scrollUp(int lines) throws IOException {
        // Optional
    }

    /**
     * Scrolls the screen down by the given number of lines.
     */
    default void scrollDown(int lines) throws IOException {
        // Optional
    }

    /**
     * Registers a handler to be called when the terminal is resized.
     *
     * @param handler the handler to call on resize
     */
    void onResize(Runnable handler);

    /**
     * Reads a single character from the terminal input with timeout.
     *
     * @param timeoutMs timeout in milliseconds
     * @return the character read, -1 for EOF, or -2 for timeout
     * @throws IOException if an I/O error occurs
     */
    int read(int timeoutMs) throws IOException;

    /**
     * Peeks at the next character without consuming it.
     *
     * @param timeoutMs timeout in milliseconds
     * @return the character peeked, -1 for EOF, or -2 for timeout
     * @throws IOException if an I/O error occurs
     */
    int peek(int timeoutMs) throws IOException;

    /**
     * Closes this backend and releases any resources.
     */
    @Override
    void close() throws IOException;
}
