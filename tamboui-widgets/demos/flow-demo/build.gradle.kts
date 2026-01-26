plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing the Flow widget with wrap layout"

demo {
    tags = setOf("flow", "layout", "wrap")
}

application {
    mainClass.set("dev.tamboui.demo.FlowDemo")
}
