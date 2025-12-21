/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.widgets.list;

import java.util.List;

/**
 * State for a List widget, tracking selection and scroll offset.
 */
public final class ListState {

    private Integer selected;
    private int offset;

    public ListState() {
        this.selected = null;
        this.offset = 0;
    }

    public Integer selected() {
        return selected;
    }

    public int offset() {
        return offset;
    }

    public void select(Integer index) {
        this.selected = index;
    }

    public void selectFirst() {
        this.selected = 0;
    }

    public void selectNext(int itemCount) {
        if (itemCount == 0) {
            return;
        }
        if (selected == null) {
            selected = 0;
        } else if (selected < itemCount - 1) {
            selected++;
        }
    }

    public void selectPrevious() {
        if (selected == null) {
            return;
        }
        if (selected > 0) {
            selected--;
        }
    }

    public void setOffset(int offset) {
        this.offset = Math.max(0, offset);
    }

    /**
     * Scrolls the list to ensure the selected item is visible.
     */
    public void scrollToSelected(int visibleHeight, List<ListItem> items) {
        if (selected == null || items.isEmpty()) {
            return;
        }

        // Calculate the top position of the selected item
        int selectedTop = 0;
        for (int i = 0; i < selected && i < items.size(); i++) {
            selectedTop += items.get(i).height();
        }
        int selectedBottom = selectedTop + items.get(selected).height();

        // Adjust offset if needed
        if (selectedTop < offset) {
            offset = selectedTop;
        } else if (selectedBottom > offset + visibleHeight) {
            offset = selectedBottom - visibleHeight;
        }
    }
}
