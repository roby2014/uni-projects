plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation(group = "org.http4k", name = "http4k-core", version = "4.40.+")
    implementation(group = "org.http4k", name = "http4k-server-jetty", version = "4.40.+")
    implementation(group = "org.http4k", name = "http4k-client-okhttp", version = "4.41.0.0")
    implementation(group = "org.http4k", name = "http4k-contract", version = "4.41.0.0")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.5.+")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.4.+")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha5")

    implementation(platform("org.http4k:http4k-bom:4.41.3.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-argo")

    implementation(platform("org.http4k:http4k-bom:4.41.4.0"))
    implementation("org.http4k:http4k-contract-ui-swagger")
    implementation("org.http4k:http4k-contract-ui-redoc")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    runtimeOnly(group = "org.slf4j", name = "slf4j-simple", version = "2.0.0-alpha5")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("pt.isel.ls.MainKt")
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pt.isel.ls.MainKt"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
