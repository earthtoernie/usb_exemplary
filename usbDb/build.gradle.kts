import java.nio.file.Paths;

plugins {
    id("myproject.java-library-conventions")
    `java-library`
}

abstract class DownloadUsbIdFile : DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.convention(project.objects.fileProperty().convention(project.layout.buildDirectory.file("usb.ids")));
    }

    @TaskAction
    fun downloadUsbIds() {
        val fileName = "usb.ids"
        val sourceUrl = "http://www.linux-usb.org/usb.ids"
        val destFile = Paths.get(project.layout.buildDirectory.get().toString(), File(fileName).toString())

        ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destFile))
    }
}

//https://github.com/gradle/kotlin-dsl-samples/blob/master/samples/task-dependencies/build.gradle.kts
//https://proandroiddev.com/the-new-way-of-writing-build-gradle-with-kotlin-dsl-script-8523710c9670
val downloadUsbIdFile : TaskProvider<DownloadUsbIdFile> =tasks.register("downloadUsbIdFile", DownloadUsbIdFile::class)


abstract class BuildUsbIdDb : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        inputFile.convention(project.objects.fileProperty().convention(project.layout.buildDirectory.file("usb.ids")));
        outputFile.convention(project.objects.fileProperty().convention(project.layout.projectDirectory.file("src/main/resources/usbids.db")));
//        outputFile.convention(project.objects.fileProperty());


    }

    @TaskAction
    fun buildDb() {
        val dbFileName = "usbids.db"
        val sourceFileName = "usb.ids"
        val destFile = Paths.get(project.layout.projectDirectory.dir("src/main/resources").toString(), File(dbFileName).toString())

        val sourceFile = Paths.get(project.layout.buildDirectory.get().toString(), File(sourceFileName).toString())

        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        usbDbBuilder.populateDB("jdbc:sqlite:$destFile", sourceFile.toString(), 200, false);
    }
}


tasks.register("buildUsbIdDb", BuildUsbIdDb::class) {
//    inputFile = downloadUsbIdFile.get().outputFile
}


tasks.register("z_countVendors") {
    dependsOn("z_downloadUsbIdFile")
    doLast {
        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        // val dbFileName = "usbids.db"
        val sourceFileName = "usb.ids"
        val sourceFile = Paths.get(buildDir.toString(), File(sourceFileName).toString())
        // val destFile = Paths.get(buildDir.toString(), File(dbFileName).toString())
        val vendorCount = usbDbBuilder.textCountVids(sourceFile.toString());
        println("**********: vendors found: $vendorCount")
    }
}

fun download(url : String, destFile : String){
    ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
}



