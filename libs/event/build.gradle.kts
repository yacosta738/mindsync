plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("dokka-conventions")
}

dependencies {
    implementation(libs.bundles.spring.boot)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")
