plugins {
    id("dev.tamboui.demo-project")
}

description = "Emoji list displayed using Columns (similar to Textual's Emoji demo)"

demo {
    tags = setOf("toolkit", "columns", "emoji", "inline", "text")
}

dependencies {
    implementation(projects.tambouiToolkit)
}

application {
    mainClass.set("EmojiListDemo")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}
