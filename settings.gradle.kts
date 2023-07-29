import org.gradle.kotlin.dsl.support.listFilesOrdered

rootProject.name = "mindsync"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version ("3.14.1")
}

fun includeProject(dir: File) {
    println("Loading submodule \uD83D\uDCE6: ${dir.name}")
    include(dir.name)
    val prj = project(":${dir.name}")
    prj.projectDir = dir
    prj.buildFileName = "${dir.name}.gradle.kts"
    require(prj.projectDir.isDirectory) { "Project '${prj.path} must have a ${prj.projectDir} directory" }
    require(prj.buildFile.isFile) { "Project '${prj.path} must have a ${prj.buildFile} build script" }
}

fun includeProjectsInDir(dirName: String) {
    file(dirName).listFilesOrdered { it.isDirectory }
        .forEach { dir ->
            includeProject(dir)
        }
}
val projects = listOf(
    "libs",
    "apps"
)
projects.forEach { includeProjectsInDir(it) }
includeProject(file("documentation"))

if (!System.getenv("CI").isNullOrEmpty() && !System.getenv("BUILD_SCAN_TOS_ACCEPTED").isNullOrEmpty()) {
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
            tag("CI")
        }
    }
}
