buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        val kotlinVersion: String by project

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("application")
    id("com.github.johnrengelman.shadow")
}

val http4kVersion: String by project
val http4kConnectVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val seaOfShadowsVersion: String by properties
val javaVersion: String by properties
val kotlinxSerializationVersion: String by properties
val kotlinxCoroutinesVersion: String by properties

application {
    mainClass = "com.example.MainKt"
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier = null
        archiveVersion = null
        mergeServiceFiles()
        dependsOn(distTar, distZip)
        isZip64 = true
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    gradlePluginPortal()
}

apply(plugin = "kotlin")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }

    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:$http4kVersion"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-cloudnative:$http4kVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4kVersion")
    implementation("org.http4k:http4k-server-undertow:$http4kVersion")
    implementation("org.http4k:http4k-client-websocket:$http4kVersion")
    implementation("org.http4k:http4k-client-jetty:$http4kVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.github.jdw.seaofshadows:seaofshadows-core:$seaOfShadowsVersion")
    implementation("com.github.jdw.seaofshadows:seaofshadows-canvas-webgl:$seaOfShadowsVersion")
}

