/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.element;

import dev.tamboui.css.Styleable;
import dev.tamboui.css.cascade.PseudoClassState;
import dev.tamboui.css.cascade.ResolvedStyle;
import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.toolkit.event.EventRouter;
import dev.tamboui.toolkit.focus.FocusManager;
import dev.tamboui.toolkit.component.ComponentTree;
import dev.tamboui.layout.Rect;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation of RenderContext with internal framework methods.
 * <p>
 * This class is used internally by the toolkit. User code should only
 * interact with the {@link RenderContext} interface.
 */
public final class DefaultRenderContext implements RenderContext {

    private final FocusManager focusManager;
    private final ComponentTree componentTree;
    private final EventRouter eventRouter;
    private final Deque<Style> styleStack = new ArrayDeque<>();
    private StyleEngine styleEngine;

    public DefaultRenderContext(FocusManager focusManager, ComponentTree componentTree, EventRouter eventRouter) {
        this.focusManager = focusManager;
        this.componentTree = componentTree;
        this.eventRouter = eventRouter;
    }

    /**
     * Creates an empty context for simple rendering without focus management.
     */
    public static DefaultRenderContext createEmpty() {
        FocusManager fm = new FocusManager();
        return new DefaultRenderContext(fm, new ComponentTree(), new EventRouter(fm));
    }

    /**
     * Sets the style engine for CSS resolution.
     *
     * @param styleEngine the style engine, or null to disable CSS
     */
    public void setStyleEngine(StyleEngine styleEngine) {
        this.styleEngine = styleEngine;
    }

    /**
     * Returns the style engine, if configured.
     */
    public Optional<StyleEngine> styleEngine() {
        return Optional.ofNullable(styleEngine);
    }

    // ═══════════════════════════════════════════════════════════════
    // Public API (from RenderContext interface)
    // ═══════════════════════════════════════════════════════════════

    @Override
    public boolean isFocused(String elementId) {
        return focusManager.isFocused(elementId);
    }

    @Override
    public boolean hasFocus() {
        return focusManager.focusedId() != null;
    }

    @Override
    public Optional<ResolvedStyle> resolveStyle(Styleable element) {
        if (styleEngine == null) {
            return Optional.empty();
        }

        // Build pseudo-class state based on focus
        PseudoClassState state = PseudoClassState.NONE;
        if (element instanceof Element) {
            Element e = (Element) element;
            if (e.id() != null && isFocused(e.id())) {
                state = PseudoClassState.ofFocused();
            }
        }

        // Build ancestor chain
        List<Styleable> ancestors = buildAncestorChain(element);

        ResolvedStyle resolved = styleEngine.resolve(element, state, ancestors);
        return resolved.hasProperties() ? Optional.of(resolved) : Optional.empty();
    }

    @Override
    public Optional<ResolvedStyle> resolveStyle(String styleType, String... cssClasses) {
        if (styleEngine == null) {
            return Optional.empty();
        }

        Set<String> classes = cssClasses.length > 0 ? Set.of(cssClasses) : Collections.emptySet();
        Styleable virtual = new VirtualStyleable(styleType, classes);
        ResolvedStyle resolved = styleEngine.resolve(virtual, PseudoClassState.NONE, Collections.emptyList());
        return resolved.hasProperties() ? Optional.of(resolved) : Optional.empty();
    }

    /**
     * A simple Styleable for resolving CSS styles by type and classes.
     */
    private static final class VirtualStyleable implements Styleable {
        private final String type;
        private final Set<String> classes;

        VirtualStyleable(String type, Set<String> classes) {
            this.type = type;
            this.classes = classes;
        }

        @Override
        public String styleType() {
            return type;
        }

        @Override
        public Optional<String> cssId() {
            return Optional.empty();
        }

        @Override
        public Set<String> cssClasses() {
            return classes;
        }

        @Override
        public Optional<Styleable> cssParent() {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Color> parseColor(String colorValue) {
        if (styleEngine == null || colorValue == null) {
            return Optional.empty();
        }
        return styleEngine.parseColor(colorValue);
    }

    @Override
    public Style currentStyle() {
        return styleStack.isEmpty() ? Style.EMPTY : styleStack.peek();
    }

    private List<Styleable> buildAncestorChain(Styleable element) {
        List<Styleable> ancestors = new ArrayList<>();
        Optional<Styleable> parent = element.cssParent();
        while (parent.isPresent()) {
            ancestors.add(0, parent.get());
            parent = parent.get().cssParent();
        }
        return ancestors;
    }

    // ═══════════════════════════════════════════════════════════════
    // Internal API (for framework use only)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Returns the focus manager.
     * <p>
     * Internal use only.
     */
    public FocusManager focusManager() {
        return focusManager;
    }

    /**
     * Returns the component tree.
     * <p>
     * Internal use only.
     */
    public ComponentTree componentTree() {
        return componentTree;
    }

    /**
     * Returns the event router.
     * <p>
     * Internal use only.
     */
    public EventRouter eventRouter() {
        return eventRouter;
    }

    /**
     * Registers an element for event routing and focus management.
     * Called by container elements after rendering children.
     * <p>
     * Internal use only.
     *
     * @param element the element to register
     * @param area the rendered area
     */
    public void registerElement(Element element, Rect area) {
        eventRouter.registerElement(element, area);
        if (element.isFocusable() && element.id() != null) {
            focusManager.registerFocusable(element.id(), area);
        }
    }

    /**
     * Executes an action with a style pushed onto the style stack.
     * <p>
     * The style is merged with the current style (current style provides base,
     * new style overrides). After the action completes, the style is popped.
     * <p>
     * Internal use only - called by StyledElement.render().
     *
     * @param style the style to push
     * @param action the action to execute
     */
    public void withStyle(Style style, Runnable action) {
        // Merge new style onto current style
        Style merged = currentStyle().patch(style);
        styleStack.push(merged);
        try {
            action.run();
        } finally {
            styleStack.pop();
        }
    }
}
