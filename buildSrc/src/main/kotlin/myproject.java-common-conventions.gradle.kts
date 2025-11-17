// Purpose: shared Java build conventions used across subprojects.
// - Configures the Java toolchain and module path inference
// - Configures test execution (JUnit Platform + JVM args)
// - Adds test dependencies (JUnit + Logback) centrally so modules don't need to declare them
// - Injects a single shared `logback-test.xml` into each project's test *output* resources
//   rather than copying it into `src/test/resources` (keeps source tree untouched)

plugins {
    java
}

version = "1.0.2"
group = "com.earthtoernie"

repositories {
    mavenCentral()
}

java {
    // Enable Gradle to try and infer the module path (JPMS) when compiling/running
    modularity.inferModulePath.set(true)
    toolchain {
        // Use the declared Java toolchain for compilation/run tasks
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

// Apply to all tests
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Needed to silence native-access warnings from sqlite-jdbc during tests
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

dependencies {
    // Centralize test dependencies so subprojects don't each add them
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
    // Provide a test-scoped SLF4J implementation (Logback) for consistent, richer test logging
    testImplementation("ch.qos.logback:logback-classic:1.4.11")
}

// --- Shared test logging resource injection ---
// We keep a single `logback-test.xml` under buildSrc/src/main/resources and add it
// to each project's `processTestResources` copy task. This ensures the shared file
// is present on the test classpath without modifying any module source files.
//
// Override behavior: if a subproject includes its own
// `src/test/resources/logback-test.xml`, that file will be preferred by the
// classpath/resource ordering and effectively override the shared one.

// Location of the shared logback-test.xml in the buildSrc project
val sharedLogback = project.rootProject.layout.projectDirectory.file("buildSrc/src/main/resources/logback-test.xml")

subprojects.forEach { sub ->
    sub.pluginManager.withPlugin("java") {
        // Configure the processTestResources Copy task to include the shared file
        // Using the Copy task API means the file is placed into the test resources output
        // directory (build/resources/test) without writing into the sources.
        sub.tasks.matching { it.name == "processTestResources" }.configureEach {
            // processTestResources is a Copy task; adding `from(...)` will copy the
            // sharedLogback into the processed test resources output
            (this as? Copy)?.from(sharedLogback)
        }
    }
}

// NOTE: There's a benign warning reported by Gradle about inconsistent JVM targets
// between Kotlin and Java compile tasks in this build (Java=25, Kotlin=24). It
// does not block the build today but can be resolved by aligning the Kotlin
// compilation target to the same Java toolchain if desired.
