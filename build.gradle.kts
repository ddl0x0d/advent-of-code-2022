import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories {
    mavenCentral()
}

val kotestVersion: String by project

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotest("runner-junit5"))
    testImplementation(kotest("assertions-core"))
    testImplementation(kotest("framework-datatest"))
}

fun kotest(module: String) = "io.kotest:kotest-$module:$kotestVersion"

application {
    mainClass.set("aoc2022.AppKt")
}

tasks.test {
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
    useJUnitPlatform()
}
