/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.toolkit.elements;

import dev.tamboui.css.cascade.CssStyleResolver;
import dev.tamboui.toolkit.element.ContainerElement;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.element.RenderContext;
import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Flex;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Margin;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.PropertyDefinition;
import dev.tamboui.style.PropertyRegistry;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.widget.Widget;
import dev.tamboui.layout.grid.Grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A CSS Grid-inspired layout container that arranges children into a grid
 * with explicit control over grid dimensions, per-column/per-row sizing
 * constraints, and gutter spacing.
 * <p>
 * Unlike {@link ColumnsElement} (which auto-calculates column count from child widths),
 * GridElement provides explicit control over grid dimensions via {@link #gridSize(int, int)},
 * per-column constraints via {@link #gridColumns(Constraint...)}, per-row constraints
 * via {@link #gridRows(Constraint...)}, and gutter spacing via {@link #gutter(int)}.
 * <p>
 * When grid-size is not set, auto-sizing uses {@code ceil(sqrt(n))} columns.
 * Column/row constraints cycle when fewer constraints than grid dimensions
 * (matching Textual behavior).
 * <p>
 * All layout properties can be set via CSS or programmatically.
 * Programmatic values override CSS values when both are set.
 * <p>
 * Supported CSS properties:
 * <ul>
 *   <li>{@code grid-size} — "3" (3 columns, auto rows) or "3 4" (3 columns, 4 rows)</li>
 *   <li>{@code grid-columns} — space-separated constraint list, e.g. "fill fill(2) 20"</li>
 *   <li>{@code grid-rows} — space-separated constraint list, e.g. "2 3"</li>
 *   <li>{@code grid-gutter} — "2" (uniform) or "1 2" (horizontal vertical)</li>
 *   <li>{@code flex} — "start", "center", "end", "space-between", "space-around", "space-evenly"</li>
 *   <li>{@code margin} — margin around the grid</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>
 * grid(text("A"), text("B"), text("C"), text("D")).gridSize(2).gutter(1)
 * </pre>
 */
public final class GridElement extends ContainerElement<GridElement> {

    /**
     * CSS property definition for the grid size.
     */
    public static final PropertyDefinition<GridSize> GRID_SIZE =
        PropertyDefinition.of("grid-size", GridSizeConverter.INSTANCE);

    /**
     * CSS property definition for per-column constraints.
     */
    @SuppressWarnings("unchecked")
    public static final PropertyDefinition<List<Constraint>> GRID_COLUMNS =
        (PropertyDefinition<List<Constraint>>) (PropertyDefinition<?>) PropertyDefinition.of("grid-columns", ConstraintListConverter.INSTANCE);

    /**
     * CSS property definition for per-row constraints.
     */
    @SuppressWarnings("unchecked")
    public static final PropertyDefinition<List<Constraint>> GRID_ROWS =
        (PropertyDefinition<List<Constraint>>) (PropertyDefinition<?>) PropertyDefinition.of("grid-rows", ConstraintListConverter.INSTANCE);

    /**
     * CSS property definition for the gutter spacing.
     */
    public static final PropertyDefinition<Gutter> GRID_GUTTER =
        PropertyDefinition.of("grid-gutter", GutterConverter.INSTANCE);

    static {
        PropertyRegistry.registerAll(GRID_SIZE, GRID_COLUMNS, GRID_ROWS, GRID_GUTTER);
    }

    private GridSize gridSize;
    private List<Constraint> gridColumns;
    private List<Constraint> gridRows;
    private Gutter gutter;
    private Margin margin;
    private Flex flex;

    /**
     * Creates an empty grid layout.
     */
    public GridElement() {
    }

    /**
     * Creates a grid layout with the given children.
     *
     * @param children the child elements
     */
    public GridElement(Element... children) {
        this.children.addAll(Arrays.asList(children));
    }

    /**
     * Creates a grid layout with the given children.
     *
     * @param children the child elements
     */
    public GridElement(Collection<? extends Element> children) {
        this.children.addAll(children);
    }

    /**
     * Sets the grid dimensions (columns and rows).
     *
     * @param cols the number of columns
     * @param rows the number of rows
     * @return this grid for method chaining
     */
    public GridElement gridSize(int cols, int rows) {
        this.gridSize = GridSize.of(cols, rows);
        return this;
    }

    /**
     * Sets the number of columns (rows auto-derived from children count).
     *
     * @param cols the number of columns
     * @return this grid for method chaining
     */
    public GridElement gridSize(int cols) {
        this.gridSize = GridSize.columns(cols);
        return this;
    }

    /**
     * Sets per-column width constraints.
     * <p>
     * If fewer constraints than columns, the constraints cycle.
     *
     * @param constraints the column constraints
     * @return this grid for method chaining
     */
    public GridElement gridColumns(Constraint... constraints) {
        this.gridColumns = Arrays.asList(constraints);
        return this;
    }

    /**
     * Sets per-row height constraints.
     * <p>
     * If fewer constraints than rows, the constraints cycle.
     *
     * @param constraints the row constraints
     * @return this grid for method chaining
     */
    public GridElement gridRows(Constraint... constraints) {
        this.gridRows = Arrays.asList(constraints);
        return this;
    }

    /**
     * Sets uniform gutter spacing between cells.
     *
     * @param value the gutter value for both horizontal and vertical
     * @return this grid for method chaining
     */
    public GridElement gutter(int value) {
        this.gutter = Gutter.uniform(value);
        return this;
    }

    /**
     * Sets asymmetric gutter spacing between cells.
     *
     * @param horizontal the horizontal gutter between columns
     * @param vertical   the vertical gutter between rows
     * @return this grid for method chaining
     */
    public GridElement gutter(int horizontal, int vertical) {
        this.gutter = Gutter.of(horizontal, vertical);
        return this;
    }

    /**
     * Sets the margin around the grid layout.
     *
     * @param margin the margin
     * @return this grid for method chaining
     */
    public GridElement margin(Margin margin) {
        this.margin = margin;
        return this;
    }

    /**
     * Sets uniform margin around the grid layout.
     *
     * @param value the margin value for all sides
     * @return this grid for method chaining
     */
    public GridElement margin(int value) {
        this.margin = Margin.uniform(value);
        return this;
    }

    /**
     * Sets how remaining space is distributed when grid constraints
     * don't fill the available area.
     *
     * @param flex the flex mode for space distribution
     * @return this grid for method chaining
     * @see Flex
     */
    public GridElement flex(Flex flex) {
        this.flex = flex;
        return this;
    }

    @Override
    public int preferredWidth() {
        if (children.isEmpty()) {
            return 0;
        }

        int cols = resolveColumns(children.size());
        int hGutter = this.gutter != null ? this.gutter.horizontal() : 0;

        int width;
        if (gridColumns != null && !gridColumns.isEmpty()) {
            // Sum constraint hints from gridColumns (cycling)
            width = 0;
            for (int c = 0; c < cols; c++) {
                Constraint constraint = gridColumns.get(c % gridColumns.size());
                width += constraintHint(constraint);
            }
        } else {
            int maxChildWidth = 0;
            for (Element child : children) {
                maxChildWidth = Math.max(maxChildWidth, child.preferredWidth());
            }
            width = maxChildWidth * cols;
        }

        width += hGutter * (cols - 1);

        if (margin != null) {
            width += margin.left() + margin.right();
        }

        return width;
    }

    @Override
    public int preferredHeight(int availableWidth, RenderContext context) {
        if (children.isEmpty() || availableWidth <= 0) {
            return 0;
        }

        int cols = resolveColumns(children.size());
        int rows = resolveRows(children.size(), cols);
        int vGutter = this.gutter != null ? this.gutter.vertical() : 0;

        int totalHeight = 0;
        if (gridRows != null && !gridRows.isEmpty()) {
            for (int r = 0; r < rows; r++) {
                Constraint constraint = gridRows.get(r % gridRows.size());
                totalHeight += constraintHint(constraint);
            }
        } else {
            int hGutter = this.gutter != null ? this.gutter.horizontal() : 0;
            int effectiveWidth = availableWidth;
            if (margin != null) {
                effectiveWidth -= margin.horizontalTotal();
            }
            int colWidth = Math.max(1, (effectiveWidth - hGutter * (cols - 1)) / cols);

            for (int row = 0; row < rows; row++) {
                int rowHeight = 1;
                for (int col = 0; col < cols; col++) {
                    int childIndex = row * cols + col;
                    if (childIndex < children.size()) {
                        Element child = children.get(childIndex);
                        rowHeight = Math.max(rowHeight, child.preferredHeight(colWidth, context));
                    }
                }
                totalHeight += rowHeight;
            }
        }

        totalHeight += vGutter * (rows - 1);

        if (margin != null) {
            totalHeight += margin.verticalTotal();
        }

        return totalHeight;
    }

    /**
     * Resolves the column count from gridSize or auto-derives from child count.
     */
    private int resolveColumns(int childCount) {
        if (gridSize != null) {
            return gridSize.columns();
        }
        // Auto: ceil(sqrt(n))
        return Math.max(1, (int) Math.ceil(Math.sqrt(childCount)));
    }

    /**
     * Resolves the row count from gridSize or auto-derives.
     */
    private int resolveRows(int childCount, int cols) {
        if (gridSize != null && gridSize.rows() > 0) {
            return gridSize.rows();
        }
        return (childCount + cols - 1) / cols;
    }

    /**
     * Extracts a size hint from a constraint for preferred width/height calculations.
     */
    private static int constraintHint(Constraint constraint) {
        if (constraint instanceof Constraint.Length) {
            return ((Constraint.Length) constraint).value();
        }
        if (constraint instanceof Constraint.Min) {
            return ((Constraint.Min) constraint).value();
        }
        if (constraint instanceof Constraint.Max) {
            return ((Constraint.Max) constraint).value();
        }
        // fill, percentage, ratio, fit — default to 1 as a minimum hint
        return 1;
    }

    @Override
    protected void renderContent(Frame frame, Rect area, RenderContext context) {
        if (children.isEmpty()) {
            return;
        }

        // Get CSS resolver for property resolution
        CssStyleResolver cssResolver = context.resolveStyle(this).orElse(null);

        // Resolve margin: programmatic > CSS > none
        Margin effectiveMargin = this.margin;
        if (effectiveMargin == null && cssResolver != null) {
            effectiveMargin = cssResolver.margin().orElse(null);
        }

        // Apply margin to get the effective render area
        Rect effectiveArea = area;
        if (effectiveMargin != null) {
            effectiveArea = effectiveMargin.inner(area);
            if (effectiveArea.isEmpty()) {
                return;
            }
        }

        // Fill background with current style
        Style effectiveStyle = context.currentStyle();
        if (effectiveStyle.bg().isPresent()) {
            frame.buffer().setStyle(effectiveArea, effectiveStyle);
        }

        // Resolve flex: programmatic > CSS > START
        Flex effectiveFlex = this.flex;
        if (effectiveFlex == null && cssResolver != null) {
            effectiveFlex = cssResolver.flex().orElse(Flex.START);
        } else if (effectiveFlex == null) {
            effectiveFlex = Flex.START;
        }

        // Resolve gridSize: programmatic > CSS > auto
        GridSize effectiveGridSize = this.gridSize;
        if (effectiveGridSize == null && cssResolver != null) {
            effectiveGridSize = cssResolver.get(GRID_SIZE).orElse(null);
        }

        // Resolve gridColumns: programmatic > CSS > null
        List<Constraint> effectiveGridColumns = this.gridColumns;
        if (effectiveGridColumns == null && cssResolver != null) {
            effectiveGridColumns = cssResolver.get(GRID_COLUMNS).orElse(null);
        }

        // Resolve gridRows: programmatic > CSS > null
        List<Constraint> effectiveGridRows = this.gridRows;
        if (effectiveGridRows == null && cssResolver != null) {
            effectiveGridRows = cssResolver.get(GRID_ROWS).orElse(null);
        }

        // Resolve gutter: programmatic > CSS > no gutter
        Gutter effectiveGutter = this.gutter;
        if (effectiveGutter == null && cssResolver != null) {
            effectiveGutter = cssResolver.get(GRID_GUTTER).orElse(null);
        }
        int hGutter = effectiveGutter != null ? effectiveGutter.horizontal() : 0;
        int vGutter = effectiveGutter != null ? effectiveGutter.vertical() : 0;

        // Compute grid dimensions
        int childCount = children.size();
        int cols;
        if (effectiveGridSize != null) {
            cols = effectiveGridSize.columns();
        } else {
            cols = Math.max(1, (int) Math.ceil(Math.sqrt(childCount)));
        }

        int rows;
        if (effectiveGridSize != null && effectiveGridSize.rows() > 0) {
            rows = effectiveGridSize.rows();
        } else {
            rows = (childCount + cols - 1) / cols;
        }

        // Compute per-row heights from children's preferred heights (when no row constraints)
        int[] rowHeights = null;
        if (effectiveGridRows == null || effectiveGridRows.isEmpty()) {
            // Build horizontal constraints to compute column widths for preferred height
            List<Constraint> hConstraints = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                if (effectiveGridColumns != null && !effectiveGridColumns.isEmpty()) {
                    hConstraints.add(effectiveGridColumns.get(c % effectiveGridColumns.size()));
                } else {
                    // Find max preferred width of children in this column
                    int maxPreferred = 0;
                    for (int row = 0; row < rows; row++) {
                        int childIndex = row * cols + c;
                        if (childIndex < childCount) {
                            maxPreferred = Math.max(maxPreferred, children.get(childIndex).preferredWidth());
                        }
                    }
                    hConstraints.add(maxPreferred > 0 ? Constraint.length(maxPreferred) : Constraint.fill());
                }
                if (hGutter > 0 && c < cols - 1) {
                    hConstraints.add(Constraint.length(hGutter));
                }
            }

            List<Rect> colAreas = Layout.horizontal()
                .constraints(hConstraints.toArray(new Constraint[0]))
                .flex(effectiveFlex)
                .split(effectiveArea);

            // Extract only column areas (skip gutter areas)
            List<Rect> columnRects = new ArrayList<>();
            for (int i = 0; i < colAreas.size(); i++) {
                if (hGutter > 0 && i % 2 == 1) {
                    continue;
                }
                columnRects.add(colAreas.get(i));
            }

            rowHeights = new int[rows];
            for (int row = 0; row < rows; row++) {
                int rowHeight = 1;
                for (int col = 0; col < cols; col++) {
                    int childIndex = row * cols + col;
                    if (childIndex < childCount) {
                        Element child = children.get(childIndex);
                        int colWidth = col < columnRects.size() ? columnRects.get(col).width() : 1;
                        rowHeight = Math.max(rowHeight, child.preferredHeight(colWidth, context));
                    }
                }
                rowHeights[row] = rowHeight;
            }
        }

        // Wrap each child Element as a lambda Widget
        List<Widget> childWidgets = new ArrayList<>(childCount);
        for (Element child : children) {
            childWidgets.add((cellArea, buf) -> context.renderChild(child, frame, cellArea));
        }

        // Build and render the Grid widget
        Grid.Builder builder = Grid.builder()
            .children(childWidgets)
            .columnCount(cols)
            .horizontalGutter(hGutter)
            .verticalGutter(vGutter)
            .flex(effectiveFlex);

        if (effectiveGridColumns != null && !effectiveGridColumns.isEmpty()) {
            builder.columnConstraints(effectiveGridColumns);
        }

        if (effectiveGridRows != null && !effectiveGridRows.isEmpty()) {
            builder.rowConstraints(effectiveGridRows);
        }

        if (rowHeights != null) {
            builder.rowHeights(rowHeights);
        }

        frame.renderWidget(builder.build(), effectiveArea);
    }
}
