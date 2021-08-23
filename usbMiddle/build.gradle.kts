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

    module("usb-api-1.0.2.jar", "javax.usb", "1.0.2"){
        exports("javax.usb")
    }
}
dependencies {
    implementation("javax.usb:usb-api:1.0.2")
}


//
//test {
//    useJUnitPlatform()
//}