/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.terminal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.tamboui.buffer.Cell;
import dev.tamboui.style.Color;
import dev.tamboui.style.Hyperlink;
import dev.tamboui.style.Style;

import static org.assertj.core.api.Assertions.assertThat;

class AnsiCellWriterTest {

    @Test
    @DisplayName("single cell emits style and symbol")
    void singleCellEmitsStyleAndSymbol() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        writer.writeCell(new Cell("A", Style.EMPTY.fg(Color.RED)));
        writer.close();

        String output = sb.toString();
        assertThat(output).contains("A");
        assertThat(output).contains(AnsiStringBuilder.styleToAnsi(Style.EMPTY.fg(Color.RED)));
        assertThat(output).endsWith(AnsiStringBuilder.RESET);
    }

    @Test
    @DisplayName("two cells with same style emit style only once")
    void sameSyleEmittedOnce() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Style style = Style.EMPTY.fg(Color.GREEN);
        writer.writeCell(new Cell("A", style));
        writer.writeCell(new Cell("B", style));
        writer.close();

        String output = sb.toString();
        String styleAnsi = AnsiStringBuilder.styleToAnsi(style);
        int firstIdx = output.indexOf(styleAnsi);
        int secondIdx = output.indexOf(styleAnsi, firstIdx + styleAnsi.length());
        assertThat(firstIdx).isGreaterThanOrEqualTo(0);
        assertThat(secondIdx).isEqualTo(-1);
        assertThat(output).contains("AB");
    }

    @Test
    @DisplayName("continuation cell is skipped")
    void continuationCellIsSkipped() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        writer.writeCell(new Cell("A", Style.EMPTY));
        writer.writeCell(Cell.CONTINUATION);
        writer.writeCell(new Cell("B", Style.EMPTY));
        writer.close();

        String output = sb.toString();
        // Only style + "A" + "B" + reset — no extra symbols from continuation
        assertThat(output).contains("AB");
    }

    @Test
    @DisplayName("close emits RESET even with no cells written")
    void closeEmitsResetWithNoCells() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        writer.close();

        assertThat(sb.toString()).isEqualTo(AnsiStringBuilder.RESET);
    }

    @Test
    @DisplayName("hyperlinked cell emits OSC8 start sequence")
    void hyperlinkCellEmitsOsc8Start() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Hyperlink link = Hyperlink.of("https://example.com");
        writer.writeCell(new Cell("X", Style.EMPTY.hyperlink(link)));
        writer.close();

        String output = sb.toString();
        assertThat(output).contains(AnsiStringBuilder.hyperlinkStart(link));
        assertThat(output).contains("X");
        assertThat(output).contains(AnsiStringBuilder.hyperlinkEnd());
    }

    @Test
    @DisplayName("close closes open hyperlink before RESET")
    void closeClosesOpenHyperlink() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        writer.writeCell(new Cell("A", Style.EMPTY.hyperlink("https://example.com")));
        writer.close();

        String output = sb.toString();
        int endIdx = output.indexOf(AnsiStringBuilder.hyperlinkEnd());
        int resetIdx = output.indexOf(AnsiStringBuilder.RESET, endIdx);
        assertThat(endIdx).isGreaterThanOrEqualTo(0);
        assertThat(resetIdx).isGreaterThan(endIdx);
    }

    @Test
    @DisplayName("hyperlink transition emits end then start")
    void hyperlinkTransitionEmitsEndThenStart() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Hyperlink linkA = Hyperlink.of("https://a.example");
        Hyperlink linkB = Hyperlink.of("https://b.example");
        writer.writeCell(new Cell("A", Style.EMPTY.hyperlink(linkA)));
        writer.writeCell(new Cell("B", Style.EMPTY.hyperlink(linkB)));
        writer.close();

        String output = sb.toString();
        int endAIdx = output.indexOf(AnsiStringBuilder.hyperlinkEnd());
        int startBIdx = output.indexOf(AnsiStringBuilder.hyperlinkStart(linkB));
        assertThat(endAIdx).isGreaterThanOrEqualTo(0);
        assertThat(startBIdx).isGreaterThan(endAIdx);
    }

    @Test
    @DisplayName("transition from hyperlink to plain emits end")
    void hyperlinkToPlainEmitsEnd() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Hyperlink link = Hyperlink.of("https://example.com");
        writer.writeCell(new Cell("A", Style.EMPTY.hyperlink(link)));
        writer.writeCell(new Cell("B", Style.EMPTY.fg(Color.RED)));
        writer.close();

        String output = sb.toString();
        int endIdx = output.indexOf(AnsiStringBuilder.hyperlinkEnd());
        assertThat(endIdx).isGreaterThanOrEqualTo(0);
        assertThat(output).contains("A");
        assertThat(output).contains("B");
    }

    @Test
    @DisplayName("same hyperlink across consecutive cells emits start only once")
    void sameHyperlinkEmittedOnce() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Hyperlink link = Hyperlink.of("https://example.com");
        Style style = Style.EMPTY.hyperlink(link);
        writer.writeCell(new Cell("A", style));
        writer.writeCell(new Cell("B", style));
        writer.close();

        String output = sb.toString();
        String startSeq = AnsiStringBuilder.hyperlinkStart(link);
        int firstIdx = output.indexOf(startSeq);
        int secondIdx = output.indexOf(startSeq, firstIdx + startSeq.length());
        assertThat(firstIdx).isGreaterThanOrEqualTo(0);
        assertThat(secondIdx).isEqualTo(-1);
    }

    @Test
    @DisplayName("multiple style changes emit new SGR for each change")
    void multipleStyleChanges() {
        StringBuilder sb = new StringBuilder();
        AnsiCellWriter writer = new AnsiCellWriter(sb::append);

        Style red = Style.EMPTY.fg(Color.RED);
        Style blue = Style.EMPTY.fg(Color.BLUE);
        writer.writeCell(new Cell("R", red));
        writer.writeCell(new Cell("B", blue));
        writer.close();

        String output = sb.toString();
        assertThat(output).contains(AnsiStringBuilder.styleToAnsi(red));
        assertThat(output).contains(AnsiStringBuilder.styleToAnsi(blue));
    }
}
