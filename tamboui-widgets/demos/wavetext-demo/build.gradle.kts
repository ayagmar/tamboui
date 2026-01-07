plugins {
    id("dev.tamboui.demo-project")
}

description = "Demo showcasing the WaveText widget"

demo {
    tags = setOf("wavetext", "animation", "text-effects")
}

application {
    mainClass.set("dev.tamboui.demo.WaveTextDemo")
}
