plugins {
    id("kotlin-conventions")
    id("spring-testing-conventions")
    id("dokka-conventions")
//  id("publishing-conventions") // If everything was configured correctly, you could use it to publish the artifacts. But it is not working with Spring as I thought.
    id("spring-conventions")
    id("sonarqube-conventions")
}

val testcontainers_version: String by project
val json_web_token_version: String by project

dependencies {
    // L O C A L   D E P E N D E N C I E S
    implementation(project(":shared"))

    // S P R I N G
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.7.0")
    implementation("com.squareup.moshi:moshi:1.10.0")
    implementation("org.keycloak:keycloak-admin-client:21.1.1")

    // 3 R D   P A R T Y
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    implementation("org.reflections:reflections:0.10.2")
    implementation(libs.bundles.arrow)

    // D A T A B A S E S
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")

    // D E V   D E P E N D E N C I E S
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // T E S T   D E P E N D E N C I E S
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainers_version")
    testImplementation("com.github.dasniko:testcontainers-keycloak:2.5.0")
    testImplementation("org.testcontainers:neo4j:$testcontainers_version")
    testImplementation("io.jsonwebtoken:jjwt-api:$json_web_token_version")
    testImplementation("io.jsonwebtoken:jjwt-impl:$json_web_token_version")
    testImplementation("io.jsonwebtoken:jjwt-jackson:$json_web_token_version")

    val os =
        org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    val arch =
        org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentArchitecture()
    if (os.isMacOsX && !arch.isAmd64) {
        implementation("io.netty:netty-resolver-dns-native-macos") {
            artifact {
                classifier = "osx-aarch_64"
            }
        }
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")
