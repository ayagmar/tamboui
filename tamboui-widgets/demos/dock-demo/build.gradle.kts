plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing the Dock widget with 5-region layout"

demo {
    tags = setOf("dock", "layout", "border-layout")
}

application {
    mainClass.set("dev.tamboui.demo.DockDemo")
}
