plugins {
    id("ink.glimt.java-library")
}

description = "High-level TUI application framework for Glimt"

dependencies {
    api(projects.glimtCore)
    api(projects.glimtWidgets)
}
