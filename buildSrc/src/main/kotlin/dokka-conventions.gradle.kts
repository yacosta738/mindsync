import io.mindsync.gradle.extension.fromComponent
import io.mindsync.gradle.extension.getGitLabToken
import io.mindsync.gradle.extension.gitLab

plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin")
}

tasks.dokkaJavadoc {
    outputDirectory.set(buildDir.resolve("javadoc"))
}
