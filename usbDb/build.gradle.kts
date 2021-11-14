import java.nio.file.Paths;

plugins {
    id("myproject.java-library-conventions")
    `java-library`
}
tasks.register("z_downloadUsbIdFile"){
    doLast {
        val fileName = "usb.ids"
        val sourceUrl = "http://www.linux-usb.org/usb.ids"
        val destFile = Paths.get(buildDir.toString(), File(fileName).toString())
        download(sourceUrl, destFile.toString())
    }
}

// !!!!!!!!!!!!!!!!!! THIS MUST BE RUN TO GET VALUES FROM DATABASE !!!!!!!!!!!!!!!!!!
// TODO check that database exists, to save time
tasks.register("z_buildUsbIdDb") {
    dependsOn("z_downloadUsbIdFile")
    val dbFileName = "usbids.db"
    val sourceFileName = "usb.ids"
    val sourceFile = Paths.get(buildDir.toString(), File(sourceFileName).toString())
    val destFile = Paths.get(layout.projectDirectory.dir("src/main/resources").toString(), File(dbFileName).toString())

    doLast {
        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        usbDbBuilder.populateDB("jdbc:sqlite:$destFile", sourceFile.toString(), 200, false);
    }
    inputs.file(sourceFile)
    outputs.file(destFile)

}

tasks.classes {
    dependsOn("z_buildUsbIdDb")
}

//tasks.register<Copy>("z_copyDatabase") {
//
//}

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



