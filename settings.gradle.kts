pluginManagement {
    repositories {
        // Repository containing prerelease Kotlin plugin artifacts
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        }
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        // Use the Kotlin Gradle plugin version requested
        id("org.jetbrains.kotlin.jvm") version "2.3.0-Beta2"
    }
}

rootProject.name = "usb-exemplary"
include("application")
include("usbMiddle")

//define arguments to decide which main to run, along with the arguments
include("usbMiddle")
include("nativeResources")
include("usbDb")
include("usbGui")
