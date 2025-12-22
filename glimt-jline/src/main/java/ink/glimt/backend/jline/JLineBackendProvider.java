/*
 * Copyright (c) 2025 Glimt Contributors
 * SPDX-License-Identifier: MIT
 */
package ink.glimt.backend.jline;

import ink.glimt.terminal.Backend;
import ink.glimt.terminal.BackendProvider;

import java.io.IOException;

/**
 * {@link BackendProvider} implementation for JLine 3.
 * <p>
 * This provider is registered via the Java {@link java.util.ServiceLoader} mechanism.
 */
public class JLineBackendProvider implements BackendProvider {

    @Override
    public Backend create() throws IOException {
        return new JLineBackend();
    }
}
