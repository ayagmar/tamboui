/*
 * Copyright (c) 2025 TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Factory for creating {@link Backend} instances using the {@link ServiceLoader} mechanism.
 * <p>
 * This factory discovers {@link BackendProvider} implementations on the classpath
 * and uses them to create backend instances. Applications should include exactly
 * one backend provider on the classpath (e.g., tamboui-jline).
 *
 * @see BackendProvider
 * @see Backend
 */
public final class BackendFactory {

    private BackendFactory() {
        // Utility class
    }

    /**
     * Creates a new backend instance using the discovered provider.
     * <p>
     * This method discovers {@link BackendProvider} implementations on the classpath
     * and selects one based on the following priority:
     * <ol>
     *   <li>System property {@code tamboui.backend} (if set)</li>
     *   <li>Environment variable {@code TAMBOUI_BACKEND} (if set)</li>
     *   <li>Auto-discovery via ServiceLoader (if exactly one provider is found)</li>
     * </ol>
     * <p>
     * The provider can be specified by:
     * <ul>
     *   <li>Simple name (e.g., "jline", "panama") - matches the provider's {@link BackendProvider#name()}</li>
     *   <li>Fully qualified class name (e.g., "dev.tamboui.backend.jline.JLineBackendProvider")</li>
     * </ul>
     * <p>
     * If multiple providers are found and none is explicitly specified, an exception is thrown
     * with a list of available providers.
     *
     * @return a new backend instance
     * @throws IOException if backend creation fails
     * @throws IllegalStateException if no provider is found, multiple providers are found without explicit selection,
     *                               or the specified provider cannot be found or instantiated
     */
    public static Backend create() throws IOException {
        // Check system property first, then environment variable
        String providerSpec = System.getProperty("tamboui.backend");
        if (providerSpec == null || providerSpec.isEmpty()) {
            providerSpec = System.getenv("TAMBOUI_BACKEND");
        }

        // If provider spec contains a dot, it's likely a class name - try loading it directly
        if (providerSpec != null && !providerSpec.isEmpty() && providerSpec.contains(".")) {
            try {
                Class<?> clazz = Class.forName(providerSpec);
                if (!BackendProvider.class.isAssignableFrom(clazz)) {
                    throw new IllegalStateException(
                        "Specified tamboui.backend class " + providerSpec + " does not implement BackendProvider"
                    );
                }
                BackendProvider provider = (BackendProvider) clazz.getDeclaredConstructor().newInstance();
                return provider.create();
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                    "Backend provider class specified in tamboui.backend not found: " + providerSpec, e
                );
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                    "Failed to instantiate Backend provider class: " + providerSpec, e
                );
            }
        }

        // Load all available providers (needed for simple name matching or auto-discovery)
        ServiceLoader<BackendProvider> loader = ServiceLoader.load(BackendProvider.class);
        List<BackendProvider> providers = new ArrayList<>();
        loader.forEach(providers::add);

        if (providers.isEmpty()) {
            throw new IllegalStateException(
                "No BackendProvider found on classpath. " +
                "Add a backend dependency such as tamboui-jline, or set the tamboui.backend system property " +
                "or TAMBOUI_BACKEND environment variable."
            );
        }

        // If a provider is explicitly specified (simple name), use it
        if (providerSpec != null && !providerSpec.isEmpty()) {
            BackendProvider selected = findProvider(providerSpec, providers);
            if (selected == null) {
                String availableProviders = formatAvailableProviders(providers);
                throw new IllegalStateException(
                    "BackendProvider '" + providerSpec + "' not found. " +
                    "Available providers:\n" + availableProviders
                );
            }
            return selected.create();
        }

        // No explicit selection - check if we have exactly one
        if (providers.size() > 1) {
            String availableProviders = formatAvailableProviders(providers);
            throw new IllegalStateException(
                "Multiple backend providers found on classpath.\n"+
                "Include only one backend or specify which provider to use by setting the tamboui.backend system property " +
                "or TAMBOUI_BACKEND environment variable to one of:\n" + availableProviders
            );
        }

        return providers.get(0).create();
    }

    /**
     * Finds a provider by simple name.
     * <p>
     * Note: Fully qualified class names are handled earlier in {@link #create()}
     * before ServiceLoader discovery, so this method only handles simple names.
     *
     * @param spec the provider specification (simple name)
     * @param providers the list of available providers
     * @return the matching provider, or null if not found
     */
    private static BackendProvider findProvider(String spec, List<BackendProvider> providers) {
        // Match by simple name (case-insensitive)
        for (BackendProvider provider : providers) {
            if (spec.equalsIgnoreCase(provider.name())) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Formats a list of providers into a user-friendly string showing both names and class names.
     *
     * @param providers the list of providers
     * @return a formatted string listing available providers
     */
    private static String formatAvailableProviders(List<BackendProvider> providers) {
        return providers.stream()
            .map(p -> p.name() + " (" + p.getClass().getName() + ")")
            .collect(Collectors.joining("\n"));
    }
}

