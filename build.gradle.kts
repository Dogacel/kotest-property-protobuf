plugins {
    idea
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "io.github.dogacel"
version = "0.0.1"

nexusPublishing {
    this.repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}
