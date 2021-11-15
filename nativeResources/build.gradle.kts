plugins {
    id("myproject.java-library-conventions")
}

version = "2.2.2"
group = "org.usb4java"

base {
    archivesBaseName = "usb4JavaNative"
}

// not used
ext {
    set("baseFiles", listOf("com/thirdparty/base/**"))
    set("extFiles", listOf("com/thirdparty/ext/**"))
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    excludes.addAll(ext.get("extFiles") as Collection<String>)
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/usb4java-1.3.0.jar"))
}


tasks.create("z_buildExt", Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    excludes.addAll(ext.get("baseFiles") as Collection<String>)
    from(sourceSets.main.get().output.classesDirs) // <-- HERE
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/usb4java-1.3.0.jar"))
}
// this one copies files to "jars"
tasks.register<Copy>("z_downloadToPrepare") {
    from(configurations.getByName("downloadOnly"))
    into(layout.buildDirectory.dir("jars"))
}

tasks.classes {
    dependsOn("z_extractAll")
}

tasks.register<Copy>("z_extractAll") {
    dependsOn("z_downloadToPrepare")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/libusb4java-1.3.0-linux-aarch64.jar"))
    exclude("META-INF/")
    into(layout.projectDirectory.dir("src/main/resources"))
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/libusb4java-1.3.0-linux-arm.jar"))
    exclude("META-INF/")
    into(layout.projectDirectory.dir("src/main/resources"))
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/libusb4java-1.3.0-linux-x86-64.jar"))
    exclude("META-INF/")
    into(layout.projectDirectory.dir("src/main/resources"))
    from(zipTree(layout.buildDirectory.dir("jars").get().toString() + "/libusb4java-1.3.0-win32-x86-64.jar"))
    exclude("META-INF/")
    into(layout.projectDirectory.dir("src/main/resources"))

}
val downloadOnly: Configuration by configurations.creating
//https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html#configurations-and-dependencies
dependencies {
    downloadOnly ("org.usb4java:libusb4java:1.3.0:linux-aarch64") {isTransitive = false}
    downloadOnly ("org.usb4java:libusb4java:1.3.0:linux-arm") {isTransitive = false}
    downloadOnly ("org.usb4java:libusb4java:1.3.0:linux-x86-64") {isTransitive = false}
    downloadOnly ("org.usb4java:libusb4java:1.3.0:win32-x86-64") {isTransitive = false}
    downloadOnly ("org.usb4java:usb4java:1.3.0") {isTransitive = false}
}

// source roots
// org/usb4java/linux-aarch64/libusb4java.so
// org/usb4java/linux-arm/libusb4java.so
// org/usb4java/linux-x86-64/libusb4java.so
// org/usb4java/win32-x86-64/libusb4java.dll


//https://gist.github.com/cholick/7177513
//https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.ConfigurationContainer.html
// https://docs.gradle.org/current/userguide/writing_build_scripts.html


// TO READ (Developing Custom Gradle Task Types) page 967
// Build script basics page 173

//https://discuss.gradle.org/t/replace-class-file-in-jar/23936 THIS IS THE ONE!
//https://stackoverflow.com/questions/51794696/how-can-i-download-and-reference-a-single-artifact-in-gradle ALSO THIS ONE!!