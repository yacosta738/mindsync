@file:Suppress("UnstableApiUsage")

import io.mindsync.gradle.constant.JDK_VERSION
import io.mindsync.gradle.constant.KOTLIN_VERSION
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("java-conventions")
    id("org.jetbrains.dokka")

    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm")

    // A tool to detect kotlin problems. It's nice, give it a try!
    id("detekt-conventions")
}

val embeddedMajorAndMinorKotlinVersion = project.getKotlinPluginVersion().substringBeforeLast(".")
if (KOTLIN_VERSION != embeddedMajorAndMinorKotlinVersion) {
    logger.warn(
        """
            ⚠️ Constant 'KOTLIN_VERSION' ($KOTLIN_VERSION) differs from embedded Kotlin version in Gradle (${project.getKotlinPluginVersion()})!\n
            ⚠️ Constant 'KOTLIN_VERSION' should be ($embeddedMajorAndMinorKotlinVersion).
        """.trimIndent()
    )
}

tasks.compileKotlin {
    logger.lifecycle("Configuring $name with version ${project.getKotlinPluginVersion()} in project ${project.name}")
    kotlinOptions {
        @Suppress("SpellCheckingInspection")
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all-compatibility")
        allWarningsAsErrors = true
        jvmTarget = JDK_VERSION
        languageVersion = KOTLIN_VERSION
        apiVersion = KOTLIN_VERSION
    }
}

tasks.compileTestKotlin {
    logger.lifecycle("Configuring $name with version ${project.getKotlinPluginVersion()} in project ${project.name}")
    kotlinOptions {
        @Suppress("SpellCheckingInspection")
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all-compatibility")
        allWarningsAsErrors = true
        jvmTarget = JDK_VERSION
        languageVersion = KOTLIN_VERSION
        apiVersion = KOTLIN_VERSION
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(
            JavaLanguageVersion.of(JDK_VERSION)
        )
    }
}

detekt {
    ignoreFailures = false
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    parallel = true
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    add("implementation", libs.findBundle("kotlinx-coroutines").get())
    add("implementation", libs.findLibrary("kotlin-reflect").get())
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin reflection library.
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
