/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.capability;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * A titled section of a capability report.
 */
public final class CapabilitySection {
    private final String source;
    private final String name;
    private final Map<String, Boolean> features;
    private final Map<String, Object> values;

    CapabilitySection(String source, String name, Map<String, Boolean> features, Map<String, Object> values) {
        this.source = source;
        this.name = name;
        this.features = Collections.unmodifiableMap(features);
        this.values = Collections.unmodifiableMap(values);
    }

    public String source() {
        return source;
    }

    public String name() {
        return name;
    }

    /**
     * Module-qualified title, e.g. {@code tamboui-core:Environment}.
     */
    public String title() {
        return source + ":" + name;
    }

    /**
     * Structured values for programmatic querying.
     */
    public Map<String, Object> values() {
        return values;
    }

    public Optional<Object> value(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(values.get(key));
    }

    public Optional<String> stringValue(String key) {
        return value(key, String.class);
    }

    /**
     * Returns the raw (non-stringified) value if present and assignable to the requested type.
     *
     * @param key  value key within this section
     * @param type desired type
     * @param <T>  desired type
     * @return typed value if present and assignable to {@code type}
     */
    public <T> Optional<T> value(String key, Class<T> type) {
        if (key == null || type == null) {
            return Optional.empty();
        }
        Object v = values.get(key);
        if (v == null) {
            return Optional.empty();
        }
        if (!type.isInstance(v)) {
            return Optional.empty();
        }
        return Optional.of(type.cast(v));
    }

    public Map<String, Boolean> features() {
        return features;
    }

    public Optional<Boolean> feature(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(features.get(key));
    }
}


