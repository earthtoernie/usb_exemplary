plugins {
    application
    id("myproject.java-conventions") // for common libs
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
    // This does not have to be a complete description (e.g. here 'org.apache.commons.collections' does not export anything here).
    // It only needs to be good enough to work in the context of this application we are building.
//    module("commons-beanutils-1.9.4.jar", "org.apache.commons.beanutils", "1.9.4") {
//        exports("org.apache.commons.beanutils")
//
//        requires("org.apache.commons.logging")
//        requires("java.sql")
//        requires("java.desktop")
//    }
//    module("commons-cli-1.4.jar", "org.apache.commons.cli", "3.2.2") {
//        exports("org.apache.commons.cli")
//    }
//    module("commons-collections-3.2.2.jar", "org.apache.commons.collections", "3.2.2")
//    automaticModule("commons-logging-1.2.jar", "org.apache.commons.logging")

    module("usb-api-1.0.2.jar", "javax.usb.API", "1.0.2"){
        exports("javax.usb")
    }

    module("usb4java-1.3.0.jar", "usb4java.JNI", "1.3.0"){
        requires("usb4java.LINUX")
        exports("org.usb4java")
    }

    // implementation class (was org.usb4java.javax)
    module("usb4java-javax-1.3.0.jar", "usb4java.IMPL", "1.3.0"){
        requires("javax.usb.API")
        requires("usb4java.JNI")
        exports("org.usb4java.javax")
    }


    module("libusb4java-1.3.0-linux-x86.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-x86-64.jar", "usb4java.LINUX", "1.3.0")
    module("libusb4java-1.3.0-win32-x86.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-win32-x86-64.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-darwin-x86-64.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-arm.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-aarch64.jar", "org.usb4java.javax", "1.3.0")


}

java {
    modularity.inferModulePath.set(true)
}

dependencies {
    implementation(project(":usbMiddle"))

//    implementation("com.google.code.gson:gson:2.8.6")           // real module
//    implementation("org.apache.commons:commons-lang3:3.10")     // automatic module
//    implementation("commons-beanutils:commons-beanutils:1.9.4") // plain library (also brings in other libraries transitively)
//    implementation("commons-cli:commons-cli:1.4")               // plain library
}

//The ‘run’ task is of type JavaExec
application {
    mainModule.set("org.gradle.sample.app")
    mainClass.set("org.gradle.sample.app.Main")
}
