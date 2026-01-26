plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing the Stack widget with overlapping layers"

demo {
    tags = setOf("stack", "layout", "overlay")
}

application {
    mainClass.set("dev.tamboui.demo.StackDemo")
}
