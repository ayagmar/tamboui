/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.capability;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Aggregated capability information from all discovered {@link CapabilityProvider}s.
 */
public final class CapabilityReport {
    private final List<CapabilitySection> sections;
    private final Map<String, CapabilitySection> sectionsByTitle;

    CapabilityReport(List<CapabilitySection> sections) {
        this.sections = Collections.unmodifiableList(sections);
        Map<String, CapabilitySection> byTitle = new LinkedHashMap<>();
        for (CapabilitySection section : sections) {
            byTitle.put(section.title(), section);
        }
        this.sectionsByTitle = Collections.unmodifiableMap(byTitle);
    }

    public List<CapabilitySection> sections() {
        return sections;
    }

    public Optional<CapabilitySection> section(String title) {
        if (title == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sectionsByTitle.get(title));
    }

    public Optional<Object> value(String sectionTitle, String key) {
        return section(sectionTitle).flatMap(s -> s.value(key));
    }

    public Optional<String> stringValue(String sectionTitle, String key) {
        return value(sectionTitle, key, String.class);
    }

    /**
     * Looks up a value by section title and key, and returns it if it matches the requested type.
     *
     * @param sectionTitle section title, e.g. {@code tamboui-core:environment}
     * @param key          value key within the section
     * @param type         desired value type
     * @param <T>          value type
     * @return typed value if present and assignable to {@code type}
     */
    public <T> Optional<T> value(String sectionTitle, String key, Class<T> type) {
        return section(sectionTitle).flatMap(s -> s.value(key, type));
    }

    public <T> Optional<T> value(CapabilityKey<T> key) {
        if (key == null) {
            return Optional.empty();
        }
        return value(key.sectionTitle(), key.key(), key.type());
    }

    public Optional<Boolean> feature(String sectionTitle, String key) {
        return section(sectionTitle).flatMap(s -> s.feature(key));
    }

    public void print(PrintStream out) {
        out.println("TamboUI capability report");
        out.println();
        for (CapabilitySection section : sections()) {
            out.println("== " + section.title());
            if (!section.features().isEmpty()) {
                for (Map.Entry<String, Boolean> entry : section.features().entrySet()) {
                    out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
            if (!section.values().isEmpty()) {
                for (Map.Entry<String, Object> entry : section.values().entrySet()) {
                    out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
            out.println();
        }
    }
}


