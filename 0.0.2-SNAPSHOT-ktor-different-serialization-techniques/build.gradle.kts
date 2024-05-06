plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("application")
}

group = "com.example"
version = "0.0.2"

application {
    mainClass.set("ApplicationKt")
}
//    project.ext.set("development", "yes")
//    val isDevelopment: Boolean = project.ext.has("development")
//    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
//}

repositories {
    mavenLocal()
    mavenCentral()
}

val kotlinVersion: String by properties
val logbackVersion: String by properties
val kotlinxSerializationVersion: String by properties
val javaVersion: String by properties
val seaOfShadowsVersion: String by properties
val ktorVersion: String by properties

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputPath = File("${project.projectDir}/output")
                outputFileName = "seaofshadows-terminal.js"
            }
        }
        //binaries.library()
        binaries.executable()
    }
    jvm() {
        withJava()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.github.jdw.seaofshadows:seaofshadows-core:$seaOfShadowsVersion")
                implementation("com.github.jdw.seaofshadows:seaofshadows-canvas-webgl:$seaOfShadowsVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinxSerializationVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("com.github.jdw.seaofshadows:seaofshadows-terminal-js:$seaOfShadowsVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
    }
}

tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "ApplicationKt"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }

    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.forEach { task ->
    if (task.name == "clean") {
        task.doFirst {
            delete(setOf("${task.project.projectDir}/output"))
        }
    }
}
