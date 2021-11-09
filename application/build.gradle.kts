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
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

//The ‘run’ task is of type JavaExec
application {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.MainUsbPrettyList")
}


tasks.register<JavaExec>("z_runSimple") {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.MainUsbSanity")
}
//https://stackoverflow.com/questions/18421857/is-it-possible-to-specify-multiple-main-classes-using-gradle-application-plugi/23742222
