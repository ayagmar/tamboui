/*
 * Copyright TamboUI Contributors
 * SPDX-License-Identifier: MIT
 */
package dev.tamboui.demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Main launcher for the TamboUI demos fat jar.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code java -jar tamboui-demos.jar} - List all available demos</li>
 *   <li>{@code java -jar tamboui-demos.jar <demo-name>} - Run a specific demo</li>
 *   <li>{@code java -jar tamboui-demos.jar <demo-name> [args...]} - Run a demo with arguments</li>
 * </ul>
 */
public class DemoLauncher {

    private static final String MANIFEST_PATH = "/demos-manifest.json";

    private DemoLauncher() {
        // Utility class - prevent instantiation
    }

    /**
     * Demo entry containing metadata and main class.
     */
    static final class DemoEntry {
        private final String id;
        private final String displayName;
        private final String description;
        private final String module;
        private final String mainClass;

        /**
         * Creates a new demo entry.
         * @param id          the unique demo ID (used for CLI)
         * @param displayName the human-friendly name of the demo
         * @param description a brief description of the demo
         * @param module      the module/category this demo belongs to
         * @param mainClass   the fully qualified main class to launch for this demo
         */
        DemoEntry(String id, String displayName, String description, String module, String mainClass) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.module = module;
            this.mainClass = mainClass;
        }

        public String id() {
            return id;
        }

        public String displayName() {
            return displayName;
        }

        public String description() {
            return description;
        }

        public String module() {
            return module;
        }

        public String mainClass() {
            return mainClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            DemoEntry that = (DemoEntry) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.displayName, that.displayName) &&
                    Objects.equals(this.description, that.description) &&
                    Objects.equals(this.module, that.module) &&
                    Objects.equals(this.mainClass, that.mainClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, displayName, description, module, mainClass);
        }

        @Override
        public String toString() {
            return "DemoEntry[" +
                    "id=" + id + ", " +
                    "displayName=" + displayName + ", " +
                    "description=" + description + ", " +
                    "module=" + module + ", " +
                    "mainClass=" + mainClass + ']';
        }

    }

    /**
     * Main entry point.
     *
     * @param args the CLI arguments
     * @throws Exception on unexpected error
     */
    public static void main(String[] args) throws Exception {
        Map<String, DemoEntry> demos = loadDemoManifest();

        if (args.length == 0) {
            printUsage(demos);
            System.exit(0);
        }

        String demoName = args[0];

        if ("--help".equals(demoName) || "-h".equals(demoName)) {
            printUsage(demos);
            System.exit(0);
        }

        if ("--list".equals(demoName) || "-l".equals(demoName)) {
            printDemoList(demos);
            System.exit(0);
        }

        String[] demoArgs = Arrays.copyOfRange(args, 1, args.length);
        launchDemo(demos, demoName, demoArgs);
    }

    private static Map<String, DemoEntry> loadDemoManifest() throws IOException {
        try (InputStream is = DemoLauncher.class.getResourceAsStream(MANIFEST_PATH)) {
            if (is == null) {
                throw new IOException("Demo manifest not found: " + MANIFEST_PATH);
            }
            return parseManifest(is);
        }
    }

    /**
     * Simple JSON parser for the demos manifest.
     * Parses the JSON without external dependencies.
     */
    private static Map<String, DemoEntry> parseManifest(InputStream is) throws IOException {
        Map<String, DemoEntry> demos = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String json = content.toString();

            // Simple parsing: find each demo object
            int idx = 0;
            while ((idx = json.indexOf("{", idx + 1)) != -1) {
                int end = json.indexOf("}", idx);
                if (end == -1) {
                    break;
                }

                String obj = json.substring(idx, end + 1);

                // Check if this is a demo entry (has mainClass field)
                if (obj.contains("\"mainClass\"")) {
                    String id = extractField(obj, "id");
                    String displayName = extractField(obj, "displayName");
                    String description = extractField(obj, "description");
                    String module = extractField(obj, "module");
                    String mainClass = extractField(obj, "mainClass");

                    if (id != null && mainClass != null) {
                        demos.put(id, new DemoEntry(id, displayName, description, module, mainClass));
                    }
                }

                idx = end;
            }
        }

        return demos;
    }

    private static String extractField(String json, String field) {
        String pattern = "\"" + field + "\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) {
            return null;
        }

        idx = json.indexOf(":", idx);
        if (idx == -1) {
            return null;
        }

        idx = json.indexOf("\"", idx);
        if (idx == -1) {
            return null;
        }

        int start = idx + 1;
        int end = start;

        // Find closing quote, handling escapes
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\' && end + 1 < json.length()) {
                end += 2; // Skip escaped character
            } else if (c == '"') {
                break;
            } else {
                end++;
            }
        }

        String value = json.substring(start, end);
        // Unescape common escape sequences
        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    private static void printUsage(Map<String, DemoEntry> demos) {
        System.out.println("TamboUI Demo Launcher");
        System.out.println("=====================");
        System.out.println();
        System.out.println("Usage: java -jar tamboui-demos.jar [options] [demo-name] [demo-args...]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help    Show this help message");
        System.out.println("  -l, --list    List all available demos");
        System.out.println();
        System.out.println("Available demos (" + demos.size() + "):");
        System.out.println();
        printDemoList(demos);
    }

    private static void printDemoList(Map<String, DemoEntry> demos) {
        // Group by module
        Map<String, List<DemoEntry>> byModule = new TreeMap<>();
        for (DemoEntry demo : demos.values()) {
            byModule.computeIfAbsent(demo.module(), k -> new ArrayList<>()).add(demo);
        }

        // Define module order
        List<String> moduleOrder = Arrays.asList("Core", "Widgets", "TUI", "Toolkit", "CSS", "Image", "Picocli", "TFX");

        // Sort each module's demos
        for (List<DemoEntry> list : byModule.values()) {
            list.sort(Comparator.comparing(DemoEntry::displayName));
        }

        // Print in order
        List<String> orderedModules = new ArrayList<>();
        for (String m : moduleOrder) {
            if (byModule.containsKey(m)) {
                orderedModules.add(m);
            }
        }
        // Add any remaining modules
        for (String m : byModule.keySet()) {
            if (!orderedModules.contains(m)) {
                orderedModules.add(m);
            }
        }

        for (String module : orderedModules) {
            List<DemoEntry> moduleDemos = byModule.get(module);
            if (moduleDemos == null) {
                continue;
            }

            System.out.println(module + ":");
            for (DemoEntry demo : moduleDemos) {
                System.out.printf("  %-30s %s%n", demo.id(), demo.displayName());
            }
            System.out.println();
        }
    }

    private static void launchDemo(Map<String, DemoEntry> demos, String name, String[] args) throws Exception {
        DemoEntry demo = demos.get(name);

        if (demo == null) {
            // Try case-insensitive match
            for (DemoEntry d : demos.values()) {
                if (d.id().equalsIgnoreCase(name)) {
                    demo = d;
                    break;
                }
            }
        }

        if (demo == null) {
            System.err.println("Unknown demo: " + name);
            System.err.println();
            System.err.println("Use --list to see available demos.");
            System.exit(1);
        }

        System.out.println("Launching: " + demo.displayName() + " (" + demo.module() + ")");
        System.out.println();

        Class<?> clazz = Class.forName(demo.mainClass());
        Method main = clazz.getMethod("main", String[].class);
        main.invoke(null, (Object) args);
    }
}
