plugins {
    id("ink.glimt.java-library")
}

description = "Fluent DSL for building TUI applications with Glimt"

dependencies {
    api(projects.glimtCore)
    api(projects.glimtWidgets)
    api(projects.glimtTui)
}
