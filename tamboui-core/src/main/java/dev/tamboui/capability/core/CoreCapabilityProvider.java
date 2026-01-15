/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.capability.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import dev.tamboui.capability.CapabilityProvider;
import dev.tamboui.capability.CapabilityReportBuilder;
import dev.tamboui.terminal.BackendProvider;

/**
 * Core capability contributor: prints what core assumes/uses and what it can infer from the environment.
 */
public final class CoreCapabilityProvider implements CapabilityProvider {

    @Override
    public String source() {
        return "tamboui-core";
    }

    @Override
    public void contribute(CapabilityReportBuilder report) {
        report.section(source(), "sysproperties")
                .kv("java.version", System.getProperty("java.version"))
                .kv("java.vendor", System.getProperty("java.vendor"))
                .kv("os.name", System.getProperty("os.name"))
                .kv("os.arch", System.getProperty("os.arch"))
                .kv("os.version", System.getProperty("os.version"))
                .kv("tamboui.backend", System.getProperty("tamboui.backend"))
                .end();

        report.section(source(), "environment")
                .kv("TERM", getenv("TERM"))
                .kv("COLORTERM", getenv("COLORTERM"))
                .kv("TERM_PROGRAM", getenv("TERM_PROGRAM"))
                .kv("TERM_PROGRAM_VERSION", getenv("TERM_PROGRAM_VERSION"))
                .kv("LC_ALL", getenv("LC_ALL"))
                .kv("LANG", getenv("LANG"))
                .kv("TAMBOUI_BACKEND", getenv("TAMBOUI_BACKEND"))
                .end();

        List<BackendProvider> providers = discoverBackends();
        CapabilityReportBuilder.Section backends = report.section(source(), "features")
                .kv("backend.count", providers.size());
        backends.feature("backend.present", !providers.isEmpty());
        backends.kv("backend.providers", String.join(",", providers.stream().map(BackendProvider::name).collect(Collectors.toList())));
        backends.end();

        // these are what LLM found as something tamboui assumes.
        // no need to print it until we actually query/adapt to it.
        /*report.section("Core terminal features used/assumed")
                .line("Screen management: alternate screen enter/leave")
                .line("Input: raw mode enable/disable, timed read/peek")
                .line("Rendering: cursor positioning + diff-based drawing")
                .line("Cursor: hide/show + set position")
                .line("Resize: onResize callback")
                .line("Optional: mouse capture, scroll up/down, raw byte output (images)")
                .end();
                */
    }

    private static List<BackendProvider> discoverBackends() {
        ServiceLoader<BackendProvider> loader = ServiceLoader.load(BackendProvider.class);
        List<BackendProvider> providers = new ArrayList<>();
        for (BackendProvider provider : loader) {
            providers.add(provider);
        }
        return providers;
    }


    private static String getenv(String name) {
        try {
            return System.getenv(name);
        } catch (SecurityException e) {
            return null;
        }
    }
}


