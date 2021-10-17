plugins {
    `java-library`
    id("myproject.java-conventions")
    id("extra-java-module-info") // apply my own plugin written in buildSrc

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

//    module("usb4java-1.3.0.jar", "usb4java.JNI", "1.3.0"){
//        requires("usb4java.LINUX")
//        requires("native_resources")
//        exports("org.usb4java")
//    }

    // implementation class (was org.usb4java.javax)
    module("usb4java-javax-1.3.0.jar", "usb4java.IMPL", "1.3.0"){
        requires("javax.usb.API")
        requires("native_resources")
        exports("org.usb4java.javax")
    }

    module("libusb4java-1.3.0-linux-x86.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-x86-64.jar", "usb4java.LINUX", "1.3.0")
    module("libusb4java-1.3.0-win32-x86.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-win32-x86-64.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-darwin-x86-64.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-arm.jar", "org.usb4java.javax", "1.3.0")
    module("libusb4java-1.3.0-linux-aarch64.jar", "org.usb4java.javax", "1.3.0")

    module("usb4java-1.3.0.jar", "org.usb4java", "1.3.0")

}
dependencies {
    implementation("javax.usb:usb-api:1.0.2") {isTransitive = true}
    implementation ("org.usb4java:usb4java-javax:1.3.0") {isTransitive = false} // has to be implementation
//    implementation ("org.usb4java:usb4java:1.3.0") {isTransitive = false} // has to be implementation
    implementation ("org.usb4java:libusb4java:1.3.0:linux-x86-64"){isTransitive = false}
    implementation(project(":nativeResources"))


}


//
//test {
//    useJUnitPlatform()
//}