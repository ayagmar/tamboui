pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("ru.vyarus.animalsniffer") version "2.0.0"
    }
}

rootProject.name = "tamboui-parent"

include(
    "tamboui-core",
    "tamboui-widgets",
    "tamboui-jline",
    "tamboui-tui",
    "tamboui-picocli",
    "tamboui-toolkit"
)

File(settingsDir, "demos").listFiles()?.forEach {
    if (it.isDirectory) {
        include("demos:${it.name}")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")