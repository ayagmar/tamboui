plugins {
    id("dev.tamboui.demo-project")
}

description = "Demonstrates RichList with rich content (any StyledElement as items)"

demo {
    displayName = "Rich List Demo"
    tags = setOf("list", "toolkit")
}

dependencies {
    implementation(projects.tambouiToolkit)
}

application {
    mainClass = "dev.tamboui.demo.RichListDemo"
}
