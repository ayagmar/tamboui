/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.element;

import dev.tamboui.css.Styleable;
import dev.tamboui.css.cascade.ResolvedStyle;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;

import java.util.Optional;

/**
 * Context provided during rendering, giving access to focus state and CSS styling.
 * <p>
 * This interface exposes only what user code needs during rendering.
 * Internal framework functionality is handled automatically.
 */
public interface RenderContext {

    /**
     * Returns whether the element with the given ID is currently focused.
     *
     * @param elementId the element ID to check
     * @return true if focused, false otherwise
     */
    boolean isFocused(String elementId);

    /**
     * Returns whether any element is currently focused.
     *
     * @return true if an element is focused
     */
    boolean hasFocus();

    /**
     * Resolves the CSS style for an element.
     * <p>
     * Returns the resolved CSS style if a StyleEngine is configured and matching
     * rules are found, or empty if no CSS styling is applicable.
     *
     * @param element the element to resolve styles for
     * @return the resolved style, or empty if no CSS is applicable
     */
    default Optional<ResolvedStyle> resolveStyle(Styleable element) {
        return Optional.empty();
    }

    /**
     * Resolves the CSS style for a virtual element with the given type and classes.
     * <p>
     * This is useful for resolving styles for sub-elements (like list items)
     * that aren't full Element instances but need CSS styling.
     *
     * @param styleType the element type (e.g., "ListItem")
     * @param cssClasses the CSS classes to apply
     * @return the resolved style, or empty if no CSS is applicable
     */
    default Optional<ResolvedStyle> resolveStyle(String styleType, String... cssClasses) {
        return Optional.empty();
    }

    /**
     * Parses a CSS color value string into a Color.
     * <p>
     * Supports named colors (e.g., "red", "blue"), hex colors (e.g., "#ff0000"),
     * and RGB notation (e.g., "rgb(255,0,0)").
     *
     * @param colorValue the CSS color value string
     * @return the parsed color, or empty if parsing fails
     */
    default Optional<Color> parseColor(String colorValue) {
        return Optional.empty();
    }

    /**
     * Returns the current style from the style stack.
     * <p>
     * This style represents the accumulated styles from parent elements
     * and should be used as the base for rendering operations.
     *
     * @return the current style, or {@link Style#EMPTY} if no style is active
     */
    default Style currentStyle() {
        return Style.EMPTY;
    }

    /**
     * Creates an empty context for simple rendering without focus management.
     * Primarily useful for testing.
     */
    static RenderContext empty() {
        return DefaultRenderContext.createEmpty();
    }
}
