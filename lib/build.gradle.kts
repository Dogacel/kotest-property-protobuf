plugins {
    kotlin("jvm")
    idea
    `java-library`
    alias(libs.plugins.protobuf)

    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
    id("org.jlleitschuh.gradle.ktlint")

    // Publishing
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotest related
    implementation(libs.kotest.junit)
    implementation(libs.kotest.assertions)
    implementation(libs.kotest.property)

    // Protobuf related
    implementation(libs.protobuf.java)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }

    // Enable Kotlin generation
    generateProtoTasks {
        all().forEach {
            it.builtins { }
        }
    }
}

sourceSets {
    test {
        proto {}
        java {}
        kotlin {}
    }
}

// Publishing

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
}

val javadocJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Javadoc JAR"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = "kotest-property-protobuf"
            version = rootProject.version.toString()

            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("kotest-property-protobuf")
                description.set("Property testing for protobuf messages using Kotest.")
                url.set("https://github.com/dogacel/kotest-property-protobuf")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("dogacel")
                        name.set("Doğaç Eldenk")
                        email.set("dogacel@gmail.copm")
                    }
                }
                scm {
                    url.set(
                        "https://github.com/dogacel/kotest-property-protobuf.git",
                    )
                }
                issueManagement {
                    url.set("https://github.com/dogacel/kotest-property-protobuf/issues")
                }
            }
        }
    }
}

signing {
    val signingKey = providers.environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers.environmentVariable("GPG_SIGNING_PASSPHRASE")

    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(
            signingKey.get(),
            signingPassphrase.get(),
        )
        val extension = extensions.getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}
