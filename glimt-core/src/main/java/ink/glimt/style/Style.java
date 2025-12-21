/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.style;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * A complete style definition including foreground, background, and modifiers.
 * Styles are immutable and composable.
 */
public final class Style {

    private final Color fg;
    private final Color bg;
    private final Color underlineColor;
    private final EnumSet<Modifier> addModifiers;
    private final EnumSet<Modifier> subModifiers;

    public static final Style EMPTY = new Style(
        null,
        null,
        null,
        EnumSet.noneOf(Modifier.class),
        EnumSet.noneOf(Modifier.class)
    );

    public Style(
        Color fg,
        Color bg,
        Color underlineColor,
        EnumSet<Modifier> addModifiers,
        EnumSet<Modifier> subModifiers
    ) {
        this.fg = fg;
        this.bg = bg;
        this.underlineColor = underlineColor;
        // Defensive copy of mutable EnumSets
        this.addModifiers = EnumSet.copyOf(addModifiers);
        this.subModifiers = EnumSet.copyOf(subModifiers);
    }

    /**
     * Creates a new style builder.
     */
    public static Style create() {
        return EMPTY;
    }

    // Foreground color methods

    public Style fg(Color color) {
        return new Style(color, bg, underlineColor, addModifiers, subModifiers);
    }

    public Style black() {
        return fg(Color.BLACK);
    }

    public Style red() {
        return fg(Color.RED);
    }

    public Style green() {
        return fg(Color.GREEN);
    }

    public Style yellow() {
        return fg(Color.YELLOW);
    }

    public Style blue() {
        return fg(Color.BLUE);
    }

    public Style magenta() {
        return fg(Color.MAGENTA);
    }

    public Style cyan() {
        return fg(Color.CYAN);
    }

    public Style white() {
        return fg(Color.WHITE);
    }

    public Style gray() {
        return fg(Color.GRAY);
    }

    // Background color methods

    public Style bg(Color color) {
        return new Style(fg, color, underlineColor, addModifiers, subModifiers);
    }

    public Style onBlack() {
        return bg(Color.BLACK);
    }

    public Style onRed() {
        return bg(Color.RED);
    }

    public Style onGreen() {
        return bg(Color.GREEN);
    }

    public Style onYellow() {
        return bg(Color.YELLOW);
    }

    public Style onBlue() {
        return bg(Color.BLUE);
    }

    public Style onMagenta() {
        return bg(Color.MAGENTA);
    }

    public Style onCyan() {
        return bg(Color.CYAN);
    }

    public Style onWhite() {
        return bg(Color.WHITE);
    }

    // Underline color

    public Style underlineColor(Color color) {
        return new Style(fg, bg, color, addModifiers, subModifiers);
    }

    // Modifier methods

    public Style addModifier(Modifier modifier) {
        EnumSet<Modifier> newAdd = EnumSet.copyOf(addModifiers);
        newAdd.add(modifier);
        EnumSet<Modifier> newSub = EnumSet.copyOf(subModifiers);
        newSub.remove(modifier);
        return new Style(fg, bg, underlineColor, newAdd, newSub);
    }

    public Style removeModifier(Modifier modifier) {
        EnumSet<Modifier> newAdd = EnumSet.copyOf(addModifiers);
        newAdd.remove(modifier);
        EnumSet<Modifier> newSub = EnumSet.copyOf(subModifiers);
        newSub.add(modifier);
        return new Style(fg, bg, underlineColor, newAdd, newSub);
    }

    public Style bold() {
        return addModifier(Modifier.BOLD);
    }

    public Style notBold() {
        return removeModifier(Modifier.BOLD);
    }

    public Style dim() {
        return addModifier(Modifier.DIM);
    }

    public Style notDim() {
        return removeModifier(Modifier.DIM);
    }

    public Style italic() {
        return addModifier(Modifier.ITALIC);
    }

    public Style notItalic() {
        return removeModifier(Modifier.ITALIC);
    }

    public Style underlined() {
        return addModifier(Modifier.UNDERLINED);
    }

    public Style notUnderlined() {
        return removeModifier(Modifier.UNDERLINED);
    }

    public Style slowBlink() {
        return addModifier(Modifier.SLOW_BLINK);
    }

    public Style rapidBlink() {
        return addModifier(Modifier.RAPID_BLINK);
    }

    public Style reversed() {
        return addModifier(Modifier.REVERSED);
    }

    public Style notReversed() {
        return removeModifier(Modifier.REVERSED);
    }

    public Style hidden() {
        return addModifier(Modifier.HIDDEN);
    }

    public Style notHidden() {
        return removeModifier(Modifier.HIDDEN);
    }

    public Style crossedOut() {
        return addModifier(Modifier.CROSSED_OUT);
    }

    public Style notCrossedOut() {
        return removeModifier(Modifier.CROSSED_OUT);
    }

    /**
     * Combines this style with another. The other style's values override this style's
     * values where they are set.
     */
    public Style patch(Style other) {
        Color newFg = other.fg != null ? other.fg : this.fg;
        Color newBg = other.bg != null ? other.bg : this.bg;
        Color newUnderlineColor = other.underlineColor != null ? other.underlineColor : this.underlineColor;

        EnumSet<Modifier> newAddModifiers = EnumSet.copyOf(this.addModifiers);
        newAddModifiers.removeAll(other.subModifiers);
        newAddModifiers.addAll(other.addModifiers);

        EnumSet<Modifier> newSubModifiers = EnumSet.copyOf(this.subModifiers);
        newSubModifiers.removeAll(other.addModifiers);
        newSubModifiers.addAll(other.subModifiers);

        return new Style(newFg, newBg, newUnderlineColor, newAddModifiers, newSubModifiers);
    }

    /**
     * Returns the effective set of modifiers (add - sub).
     */
    public EnumSet<Modifier> effectiveModifiers() {
        EnumSet<Modifier> result = EnumSet.copyOf(addModifiers);
        result.removeAll(subModifiers);
        return result;
    }

    public Optional<Color> fg() {
        return Optional.ofNullable(fg);
    }

    public Optional<Color> bg() {
        return Optional.ofNullable(bg);
    }

    public Optional<Color> underlineColor() {
        return Optional.ofNullable(underlineColor);
    }

    public EnumSet<Modifier> addModifiers() {
        return EnumSet.copyOf(addModifiers);
    }

    public EnumSet<Modifier> subModifiers() {
        return EnumSet.copyOf(subModifiers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Style)) {
            return false;
        }
        Style style = (Style) o;
        return Objects.equals(fg, style.fg)
            && Objects.equals(bg, style.bg)
            && Objects.equals(underlineColor, style.underlineColor)
            && addModifiers.equals(style.addModifiers)
            && subModifiers.equals(style.subModifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fg, bg, underlineColor, addModifiers, subModifiers);
    }

    @Override
    public String toString() {
        return String.format(
            "Style[fg=%s, bg=%s, underlineColor=%s, addModifiers=%s, subModifiers=%s]",
            fg, bg, underlineColor, addModifiers, subModifiers);
    }
}
