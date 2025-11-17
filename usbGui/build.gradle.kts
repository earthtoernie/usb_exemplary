plugins {
    id("myproject.java-library-conventions")
//    id("extra-java-module-info") // apply my own plugin written in buildSrc
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


var currentOS = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem();
val osArch = System.getProperty("os.arch")
println("System Architecture**********: $osArch")
var platform: String = ""
if (currentOS.isWindows) {
    platform = "win"
} else if (currentOS.isLinux) {
    platform = "linux"
    platform = "linux"
    if (osArch == "aarch64") {
        platform = "linux-aarch64"
    }
} else if (currentOS.isFreeBSD) {
    platform = "mac"
}

dependencies {
    implementation("org.openjfx:javafx-base:25:${platform}")
    implementation("org.openjfx:javafx-controls:25:${platform}")
    implementation ("org.openjfx:javafx-graphics:25:${platform}")
    implementation("org.openjfx:javafx-fxml:25:${platform}")
    implementation(project(":usbMiddle"))
    implementation("javax.usb:usb-api:1.0.2") {isTransitive = false}

    implementation(project(":usbDb"))
}