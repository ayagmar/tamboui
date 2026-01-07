plugins {
    id("dev.tamboui.demo-project")
}

description = "Codex-like AI coding assistant demo"

demo {
    displayName = "AI Coding Assistant Demo"
    tags = setOf("toolkit", "css", "bindings", "progress", "animation", "chat")
}

dependencies {
    implementation(projects.tambouiToolkit)
    implementation(projects.tambouiCss)
}

application {
    mainClass = "dev.tamboui.demo.coding.CodingAssistantDemo"
}
