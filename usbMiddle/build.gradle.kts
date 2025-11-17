plugins {
    id("myproject.java-library-conventions")
    id("extra-java-module-info") // apply my own plugin written in buildSrc
}

extraJavaModuleInfo {

    module("usb-api-1.0.2.jar", "javax.usb.API", "1.0.2"){
        exports("javax.usb")
    }

    // implementation class (was org.usb4java.javax)
    module("usb4java-javax-1.3.0.jar", "usb4java.IMPL", "1.3.0"){
        requires("javax.usb.API")
        requires("native_resources")
        exports("org.usb4java.javax")
    }

}
dependencies {
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("javax.usb:usb-api:1.0.2") {isTransitive = false}
    implementation ("org.usb4java:usb4java-javax:1.3.0") {isTransitive = false} // has to be implementation
    implementation(project(":nativeResources"))
    implementation(project(":usbDb"))
}