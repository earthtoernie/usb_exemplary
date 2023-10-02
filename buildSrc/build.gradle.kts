plugins {
    `kotlin-dsl` // used for the conventions plugin
    `java-gradle-plugin` // so we can assign and ID to our plugin
}

dependencies {
    implementation("org.ow2.asm:asm:8.0.1")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
    }
}

gradlePlugin {
    plugins {
        // here we register our plugin with an ID
        register("extra-java-module-info") {
            id = "extra-java-module-info"
            implementationClass = "org.gradle.sample.transform.javamodules.ExtraModuleInfoPlugin"
        }
    }
}
