plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.org.cadixdev.gradle:licenser:0.6.1")
}

tasks {
    compileJava {
        options.release.set(21)
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}