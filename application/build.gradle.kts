plugins {
    application
    id("myproject.java-common-conventions") // for common libs
    id("extra-java-module-info") // apply my own plugin written in buildSrc
}

version = "1.0.2"
group = "org.gradle.sample"

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

var currentOS = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem();
val osArch = System.getProperty("os.arch")
println("System Architecture**********: $osArch") // amd64 on ubuntu, aarch64 on raspi ubuntu

var platform: String = ""
if (currentOS.isWindows) {
    platform = "win"
} else if (currentOS.isLinux) {
    platform = "linux"
    if (osArch == "aarch64") {
        platform = "linux-aarch64"
    }
} else if (currentOS.isFreeBSD) {
    platform = "mac"
}

dependencies {
    implementation(project(":usbMiddle"))
    implementation(project(":usbGui"))

    implementation("org.openjfx:javafx-base:21:${platform}")
    implementation("org.openjfx:javafx-controls:21:${platform}")
    implementation ("org.openjfx:javafx-graphics:21:${platform}")
    implementation("org.openjfx:javafx-fxml:21:${platform}")

    implementation("org.apache.commons:commons-lang3:3.13.0")
}

//The ‘run’ task is of type JavaExec
application {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.MainUsbGui")
//    mainClass.set("org.gradle.sample.app.MainUsbPrettyList")
//    mainClass.set("org.gradle.sample.app.Main")

}


tasks.register<JavaExec>("z_runSimple") {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.MainUsbSanity")
}
//https://stackoverflow.com/questions/18421857/is-it-possible-to-specify-multiple-main-classes-using-gradle-application-plugi/23742222
