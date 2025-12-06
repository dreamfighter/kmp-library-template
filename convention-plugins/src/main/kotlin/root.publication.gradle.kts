import java.util.Properties

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

allprojects {
    group = "io.github.dreamfighter.kmp"
    version = "1.0.0"
}

val localProperties = File(rootDir, "local.properties").inputStream().use {
    Properties().apply { load(it) }
}

/* use this for publishing using plugins io.github.gradle-nexus.publish-plugin*/
nexusPublishing {
    // Configure maven central repository
    // https://github.com/gradle-nexus/publish-plugin#publishing-to-maven-central-via-sonatype-ossrh
    repositories {

        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            username = localProperties["sonatype.username"].toString()
            password = localProperties["sonatype.password"].toString()
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
