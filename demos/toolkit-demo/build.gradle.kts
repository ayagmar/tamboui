plugins {
    id("ink.glimt.demo-project")
}

description = "Demo showcasing the DSL module with Widget Playground"

dependencies {
    implementation(projects.glimtToolkit)
}

application {
    mainClass.set("ink.glimt.demo.ToolkitDemo")
}
