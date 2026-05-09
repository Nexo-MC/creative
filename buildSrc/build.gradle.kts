plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}


tasks {
    compileJava {
        options.release.set(21)
    }
    compileKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}