/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.css;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for elements that can be styled via CSS.
 * <p>
 * Elements implementing this interface can be targeted by CSS selectors
 * and have CSS rules applied to them during rendering.
 */
public interface Styleable {

    /**
     * Returns the style type name used for type selectors.
     * <p>
     * For example, a Panel element would return "Panel" to match
     * the CSS selector {@code Panel { ... }}.
     *
     * @return the style type name (never null)
     */
    default String styleType() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the element ID for ID selectors.
     * <p>
     * For example, an element with ID "sidebar" would be matched
     * by the CSS selector {@code #sidebar { ... }}.
     *
     * @return the element ID, or empty if not set
     */
    Optional<String> cssId();

    /**
     * Returns the CSS classes assigned to this element.
     * <p>
     * For example, an element with class "primary" would be matched
     * by the CSS selector {@code .primary { ... }}.
     *
     * @return an unmodifiable set of CSS class names (never null, may be empty)
     */
    Set<String> cssClasses();

    /**
     * Returns the parent element in the style hierarchy.
     * <p>
     * Used for descendant and child combinator selectors like
     * {@code Panel Button { ... }} or {@code Panel > Button { ... }}.
     *
     * @return the parent element, or empty if this is a root element
     */
    Optional<Styleable> cssParent();
}
