package io.mindsync.documentation

import org.asciidoctor.gradle.jvm.AsciidoctorTask
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("org.asciidoctor.jvm.convert")
}

repositories {
    mavenCentral()
}

val revDate: String = System.getenv()["revdate"] ?: LocalDateTime.now().format(
    DateTimeFormatter.ofPattern("yyyy-MM-dd")
)
val revNumber: String = System.getenv()["revnumber"] ?: "DEV-Version"

val asciidoctorTask = tasks.named<AsciidoctorTask>("asciidoctor") {
    setSourceDir(file("docs"))
    setOutputDir(file("${layout.buildDirectory}/docs"))

    resources {
        from("docs/resources") {
            include("*.png")
        }

        into("./resources")
    }

    attributes(
        mapOf(
            "source-highlighter" to "rouge",
            "toc" to "left",
            "toclevels" to 2,
            "idprefix" to "",
            "idseparator" to "-",
            "revnumber" to revNumber,
            "revdate" to revDate
        )
    )
}

configurations.create("asciidoctorHtmlFolder") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("asciidoc-html-folder"))
    }

    outgoing.artifact(
        asciidoctorTask.map { task ->
            task.outputDir
        }
    )
}
