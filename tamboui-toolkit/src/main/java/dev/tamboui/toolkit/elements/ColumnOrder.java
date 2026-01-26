/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.elements;

/**
 * Controls the ordering of children within a {@link Columns} layout.
 *
 * <ul>
 *   <li>{@link #ROW_FIRST} — items fill left-to-right, then top-to-bottom (like reading text)</li>
 *   <li>{@link #COLUMN_FIRST} — items fill top-to-bottom, then left-to-right (like newspaper columns)</li>
 * </ul>
 *
 * <p>Can be set via CSS with the {@code column-order} property:
 * <pre>
 * .my-columns { column-order: column-first; }
 * </pre>
 */
public enum ColumnOrder {

    /**
     * Items fill left-to-right, then top-to-bottom.
     */
    ROW_FIRST,

    /**
     * Items fill top-to-bottom, then left-to-right.
     */
    COLUMN_FIRST
}