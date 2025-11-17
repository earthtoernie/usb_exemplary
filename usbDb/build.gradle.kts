plugins {
    id("myproject.java-library-conventions")
    `java-library`
}

// Allow skipping the external usb.ids download via -PskipUsbDownload=true
val skipUsbDownload: Boolean = (project.findProperty("skipUsbDownload")?.toString() ?: "false") == "true"

// Register a download task that writes to build/usb.ids. It's only run when not skipping.
val downloadUsbIdFile = tasks.register("downloadUsbIdFile") {
    // declare output so Gradle knows about produced file
    outputs.file(layout.buildDirectory.file("usb.ids"))
    onlyIf { !skipUsbDownload }
    doLast {
        val sourceUrl = "http://www.linux-usb.org/usb.ids"
        val destFile = layout.buildDirectory.file("usb.ids").get().asFile
        ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destFile))
    }
}

// Register a task that builds the usbids.db from build/usb.ids, but skip gracefully if the input is absent.
val buildUsbIdDb = tasks.register("buildUsbIdDb") {
    dependsOn(downloadUsbIdFile)
    // declare output
    outputs.file(layout.projectDirectory.file("src/main/resources/usbids.db"))

    doLast {
        val inputFile = layout.buildDirectory.file("usb.ids").get().asFile
        if (!inputFile.exists()) {
            logger.lifecycle("Skipping buildUsbIdDb because input usb.ids is not present (skipUsbDownload=$skipUsbDownload)")
            return@doLast
        }

        val destFile = project.file("src/main/resources/usbids.db").toPath()
        val sourceFile = inputFile.toPath()

        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        usbDbBuilder.populateDB("jdbc:sqlite:$destFile", sourceFile.toString(), 200, false)
    }
}


// Ensure resources processing depends on building the DB (which may be a no-op if skipped)
tasks.processResources {
    dependsOn(buildUsbIdDb)
}

// A helper task used previously
tasks.register("z_countVendors") {
    dependsOn(downloadUsbIdFile)
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
