plugins {
    `java-library`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val junitVersion = "5.13.0"
    val platformVersion = "1.13.0"

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-commons:$platformVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$platformVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:$platformVersion")
}

java {
    //withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


tasks {
    //javadoc {
    //    isFailOnError = false
    //    (options as StandardJavadocDocletOptions).run {
    //        tags("sinceMinecraft:a:Since Minecraft:")
    //        tags("sincePackFormat:a:Since Resource-Pack Format:")
    //    }
    //}
    test {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }
}