plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing custom CSS-styled components"

demo {
    tags = setOf("toolkit", "custom-component", "css", "panel", "gauge", "mouse")
}

dependencies {
    implementation(projects.tambouiToolkit)
}

application {
    mainClass.set("dev.tamboui.demo.CustomComponentDemo")
}
