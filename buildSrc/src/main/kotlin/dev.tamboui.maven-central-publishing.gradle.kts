plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

group = "dev.tamboui"

nexusPublishing {
    repositories {
        sonatype {
            username.set(providers.envOrSys("central.username"))
            password.set(providers.envOrSys("central.password"))
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}

fun ProviderFactory.envOrSys(propName: String): Provider<String> =
    environmentVariable(propName.uppercase().replace(".", "_"))
        .orElse(systemProperty(propName))
