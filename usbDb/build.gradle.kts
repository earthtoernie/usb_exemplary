import java.nio.file.Paths;

plugins {
    id("myproject.java-library-conventions")
//    id("extra-java-module-info")
    `java-library`
}
tasks.register("downloadUsbIdFile"){
    doLast {
        val fileName = "usb.ids"
        val sourceUrl = "http://www.linux-usb.org/usb.ids"
//        var homePath = System.getProperty("user.home");
        val destFile = Paths.get(buildDir.toString(), File(fileName).toString())
//    val destFile = Paths.get(homePath.toString(), File(fileName).toString())
        download(sourceUrl, destFile.toString())
    }

}

tasks.register("buildUsbIdDb") {
    dependsOn("downloadUsbIdFile")
    doLast {
        val usbDbBuilder = com.earthtoernie.buildsrc.UsbDbBuilder()
        val dbFileName = "usbids.db"
        val sourceFileName = "usb.ids"
        val sourceFile = Paths.get(buildDir.toString(), File(sourceFileName).toString())
        val destFile = Paths.get(buildDir.toString(), File(dbFileName).toString())
        usbDbBuilder.populateDB("jdbc:sqlite:$destFile", sourceFile.toString());
    }
}

fun download(url : String, destFile : String){
    ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
}

tasks.assemble {
    dependsOn("buildUsbIdDb")
}
//extraJavaModuleInfo {
//    module("commons-lang3-3.12.0.jar", "org.apache.commons.lang3", "3.12.0")
//    automaticModule("commons-lang3-3.12.0.jar", "org.apache.commons.lang3")
//}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

}



