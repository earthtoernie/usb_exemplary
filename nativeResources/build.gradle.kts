plugins {
    `java-library`
}
//
//repositories {
//    mavenCentral()
//}
//
//
//myExtraDependency = configurations.create
//
//
//dependencies {
//    myExtraDependency ("org.usb4java:libusb4java:1.3.0:linux-x86-64"){isTransitive = false}
//
//}
tasks.register("bar") {

}

tasks.register("foo") {
    dependsOn("bsdfsadfar")

}


// connect to assemble assemble.dependsOn(task)

println(project.name)
//https://gist.github.com/cholick/7177513
//https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.ConfigurationContainer.html
// https://docs.gradle.org/current/userguide/writing_build_scripts.html


// TO READ (Developing Custom Gradle Task Types) page 967
// Build script basics page 173