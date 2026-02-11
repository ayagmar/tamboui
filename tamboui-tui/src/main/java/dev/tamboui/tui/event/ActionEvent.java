/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.tui.event;

/**
 * Represents a programmatically fired action, as opposed to a raw input event.
 * <p>
 * ActionEvent is created when an action is fired via
 * {@link dev.tamboui.tui.bindings.ActionHandler#fire(String)} without an
 * originating input event. It carries the action name that was fired.
 */
public final class ActionEvent implements Event {

    private final String action;

    /**
     * Creates an action event for the given action name.
     *
     * @param action the action name that was fired
     */
    public ActionEvent(String action) {
        this.action = action;
    }

    /**
     * Returns the action name that was fired.
     *
     * @return the action name
     */
    public String action() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionEvent)) {
            return false;
        }
        ActionEvent that = (ActionEvent) o;
        return action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return action.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ActionEvent[action=%s]", action);
    }
}