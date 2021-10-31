plugins {
    application
    id("myproject.java-common-conventions") // for common libs
    id("extra-java-module-info") // apply my own plugin written in buildSrc
}

version = "1.0.2"
group = "org.gradle.sample"

repositories {
    mavenCentral()
}

tasks.compileJava {
    options.javaModuleVersion.set(provider({ project.version as String }))
}

extraJavaModuleInfo {

    module("usb-api-1.0.2.jar", "javax.usb.API", "1.0.2"){
        exports("javax.usb")
    }


    // implementation class (package org.usb4java.javax), USB impl, decoupled from hardware
    module("usb4java-javax-1.3.0.jar", "usb4java.IMPL", "1.3.0"){
        requires("org.apache.commons.lang3")
        requires("javax.usb.API")
        requires("native_resources")
        exports("org.usb4java.javax")
    }

}

java {
    modularity.inferModulePath.set(true)
}

dependencies {
    implementation(project(":usbMiddle"))
    implementation(project(":nativeResources"))
    implementation("org.apache.commons:commons-lang3:3.12.0")

//    implementation("com.google.code.gson:gson:2.8.6")           // real module
//    implementation("org.apache.commons:commons-lang3:3.10")     // automatic module
//    implementation("commons-beanutils:commons-beanutils:1.9.4") // plain library (also brings in other libraries transitively)
//    implementation("commons-cli:commons-cli:1.4")               // plain library
}

//The ‘run’ task is of type JavaExec
application {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.MainUsbSanity")
}
