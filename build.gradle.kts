plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("aoc2022.AppKt")
}
