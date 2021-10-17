plugins {
    `java-library`
    id("myproject.java-conventions")
}

repositories {
    mavenCentral()
}
version = "2.2.2"
group = "org.usb4java"

base {
    archivesBaseName = "usb4JavaNative"
}

ext {
    set("baseFiles", listOf("com/thirdparty/base/**"))
    set("extFiles", listOf("com/thirdparty/ext/**"))

}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    excludes.addAll(ext.get("extFiles") as Collection<String>)
    from(zipTree(layout.buildDirectory.dir("foo").get().toString() + "/usb4java-1.3.0.jar"))


}


tasks.create("buildExt", Jar::class) {
    dependsOn("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    excludes.addAll(ext.get("baseFiles") as Collection<String>)
    from(sourceSets.main.get().output.classesDirs) // <-- HERE
    from(zipTree(layout.buildDirectory.dir("foo").get().toString() + "/bar.jar"))
}

tasks.register<Copy>("downloadToPrepare") { // TODO this has to be triggered
    from(configurations.getByName("downloadOnly"))
    into(layout.buildDirectory.dir("foo"))
}

val downloadOnly: Configuration by configurations.creating
//https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html#configurations-and-dependencies
dependencies {
    downloadOnly ("org.usb4java:usb4java:1.3.0") {isTransitive = false}
}


//https://gist.github.com/cholick/7177513
//https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.ConfigurationContainer.html
// https://docs.gradle.org/current/userguide/writing_build_scripts.html


// TO READ (Developing Custom Gradle Task Types) page 967
// Build script basics page 173

//https://discuss.gradle.org/t/replace-class-file-in-jar/23936 THIS IS THE ONE!
//https://stackoverflow.com/questions/51794696/how-can-i-download-and-reference-a-single-artifact-in-gradle ALSO THIS ONE!!