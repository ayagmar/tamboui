plugins {
    `java-library`
    id("dev.tamboui.publishing")
    id("ru.vyarus.animalsniffer")
}

dependencies {
    val libs = versionCatalogs.named("libs")
    testImplementation(platform(libs.findLibrary("junit.bom").orElseThrow()))
    testImplementation(libs.findBundle("testing").orElseThrow())
    signature("org.codehaus.mojo.signature:java18:1.0@signature")
}
