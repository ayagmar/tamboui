/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.capability;

import java.util.Objects;

/**
 * A strongly typed key for looking up a capability value.
 *
 * @param <T> value type
 */
public final class CapabilityKey<T> {
    private final String sectionTitle;
    private final String key;
    private final Class<T> type;

    private CapabilityKey(String sectionTitle, String key, Class<T> type) {
        this.sectionTitle = Objects.requireNonNull(sectionTitle, "sectionTitle");
        this.key = Objects.requireNonNull(key, "key");
        this.type = Objects.requireNonNull(type, "type");
    }

    public static <T> CapabilityKey<T> of(String sectionTitle, String key, Class<T> type) {
        return new CapabilityKey<>(sectionTitle, key, type);
    }

    public String sectionTitle() {
        return sectionTitle;
    }

    public String key() {
        return key;
    }

    public Class<T> type() {
        return type;
    }
}


