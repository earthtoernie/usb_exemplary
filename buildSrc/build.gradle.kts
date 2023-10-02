plugins {
    `kotlin-dsl` // used for the conventions plugin
    `java-gradle-plugin` // so we can assign and ID to our plugin
}

dependencies {
    implementation("org.ow2.asm:asm:9.4")
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("commons-io:commons-io:2.11.0")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

// analogous to compileKotlin task, this whole block can be removed now
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions {
//        jvmTarget = "17"
//    }
//
//}

gradlePlugin {
    plugins {
        // here we register our plugin with an ID
        register("extra-java-module-info") {
            id = "extra-java-module-info"
            implementationClass = "org.gradle.sample.transform.javamodules.ExtraModuleInfoPlugin"
        }
    }
}
