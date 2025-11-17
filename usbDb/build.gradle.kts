import java.nio.file.Paths

plugins {
    id("myproject.java-library-conventions")
    `java-library`
}

abstract class DownloadUsbIdFile : DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.convention(project.objects.fileProperty().convention(project.layout.buildDirectory.file("usb.ids")))
    }

    @TaskAction
    fun downloadUsbIds() {
        val sourceUrl = "http://www.linux-usb.org/usb.ids"
        val destFile = outputFile.get().asFile
        ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destFile))
    }
}

//https://github.com/gradle/kotlin-dsl-samples/blob/master/samples/task-dependencies/build.gradle.kts
//https://proandroiddev.com/the-new-way-of-writing-build-gradle-with-kotlin-dsl-script-8523710c9670
val downloadUsbIdFile: TaskProvider<DownloadUsbIdFile> = tasks.register("downloadUsbIdFile", DownloadUsbIdFile::class)

abstract class BuildUsbIdDb : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        inputFile.convention(project.objects.fileProperty().convention(project.layout.buildDirectory.file("usb.ids")))
        outputFile.convention(project.objects.fileProperty().convention(project.layout.projectDirectory.file("src/main/resources/usbids.db")))
    }

    @TaskAction
    fun buildDb() {
        val destFile = outputFile.get().asFile.toPath()
        val sourceFile = inputFile.get().asFile.toPath()

        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        usbDbBuilder.populateDB("jdbc:sqlite:$destFile", sourceFile.toString(), 200, false)
    }
}


tasks.register("buildUsbIdDb", BuildUsbIdDb::class) {
    dependsOn(downloadUsbIdFile)
}

tasks.processResources {
    dependsOn("buildUsbIdDb")
}


tasks.register("z_countVendors") {
    dependsOn("z_downloadUsbIdFile")
    doLast {
        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        val sourceFile = project.layout.buildDirectory.file("usb.ids").get().asFile.toPath()
        val vendorCount = usbDbBuilder.textCountVids(sourceFile.toString())
        println("**********: vendors found: $vendorCount")
    }
}

fun download(url: String, destFile: String) {
    ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("commons-io:commons-io:2.16.1")
}
