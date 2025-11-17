import java.net.URLClassLoader as JURLClassLoader
import java.io.File as JFile
import java.io.FileOutputStream as JFileOutputStream

plugins {
    id("myproject.java-library-conventions")
    `java-library`
}

// Allow skipping the external usb.ids download via -PskipUsbDownload=true
val skipUsbDownload: Boolean = (project.findProperty("skipUsbDownload")?.toString() ?: "false") == "true"

// Provide a configuration to fetch runtime jars for 7z handling used by build tasks (separate from project compile lifecycle)
val tools = configurations.create("tools")
dependencies {
    tools("org.apache.commons:commons-compress:1.26.0")
    tools("org.tukaani:xz:1.9")
}

// Task to extract usb.ids from usb.ids.7z into build/usb.ids
val extractUsbId by tasks.registering(JavaExec::class) {
    group = "usbDb"
    description = "Extract usb.ids from usb.ids.7z into build/usb.ids"
    dependsOn(tasks.named("compileJava"))
    mainClass.set("com.earthtoernie.tools.Extract7z")
    classpath = sourceSets.main.get().runtimeClasspath
    workingDir = file(".")
}

// Register a task that builds the usbids.db from build/usb.ids, but skip gracefully if the input is the placeholder.
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

        val content = try { inputFile.readText().trim() } catch (t: Throwable) { "" }
        if (content == "empty file" || content.isBlank()) {
            logger.lifecycle("Input usb.ids is placeholder/empty; skipping building usbids.db (skipUsbDownload=$skipUsbDownload)")
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

// Helper to run code with a URLClassLoader loaded from the 'tools' configuration
fun <T> withToolsClassLoader(action: (JURLClassLoader) -> T): T {
    val files = tools.resolve()
    val urls = files.map { it.toURI().toURL() }.toTypedArray()
    val loader = JURLClassLoader(urls, null)
    try {
        return action(loader)
    } finally {
        try { loader.close() } catch (ignored: Exception) {}
    }
}


// Modify downloadUsbIdFile: when skip, do NOT depend on createIds7z (archive already present)
val downloadUsbIdFile = tasks.register("downloadUsbIdFile") {
    // no createIds7z dependency; expect usbDb/usb.ids.7z to be present when skipping

    // declare output so Gradle knows about produced file
    val out = layout.buildDirectory.file("usb.ids")
    outputs.file(out)

    doLast {
        val destFile = out.get().asFile
        try {
            if (skipUsbDownload) {
                logger.lifecycle("skipUsbDownload=true: extracting usb.ids from usbDb/usb.ids.7z to ${destFile.absolutePath}")
                destFile.parentFile.mkdirs()

                val archiveCandidates = listOf(project.file("usb.ids.7z"), rootProject.file("usbDb/usb.ids.7z"), rootProject.file("usb.ids.7z"))
                val archive = archiveCandidates.firstOrNull { it.exists() }
                    ?: throw GradleException("No usb.ids.7z found to extract from; looked for ${archiveCandidates}")

                withToolsClassLoader { cl ->
                    val SevenZFile = cl.loadClass("org.apache.commons.compress.archivers.sevenz.SevenZFile")
                    val SevenZArchiveEntry = cl.loadClass("org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry")

                    val ctor = SevenZFile.getConstructor(JFile::class.java)
                    val getNextEntry = SevenZFile.getMethod("getNextEntry")
                    val readMethod = SevenZFile.getMethod("read", ByteArray::class.java)

                    val szf = ctor.newInstance(archive)
                    val fos = JFileOutputStream(destFile)
                    try {
                        var extracted = false
                        var extractedEntryName: String? = null
                        while (true) {
                            val entry = getNextEntry.invoke(szf) ?: break
                            val name = SevenZArchiveEntry.getMethod("getName").invoke(entry) as String
                            // Accept only 'usb.ids' inside the archive (we always store it as usb.ids)
                            if (name == "usb.ids") {
                                extractedEntryName = name
                                val buf = ByteArray(8192)
                                while (true) {
                                    val r = readMethod.invoke(szf, buf) as Int
                                    if (r <= 0) break
                                    fos.write(buf, 0, r)
                                }
                                extracted = true
                                break
                            }
                        }
                        if (!extracted) {
                            throw GradleException("usb.ids not found inside ${archive.absolutePath}")
                        } else {
                            // Print the concrete extracted entry name (fall back to 'unknown' if null)
                            val extractedName = extractedEntryName ?: "unknown"
                            logger.lifecycle("Extracted entry '$extractedName' from ${archive.absolutePath} to ${destFile.absolutePath}")
                        }
                    } finally {
                        try { fos.close() } catch (ignored: Exception) {}
                        try { SevenZFile.getMethod("close").invoke(szf) } catch (ignored: Exception) {}
                    }
                }

                return@doLast
            } else {
                val sourceUrl = "http://www.linux-usb.org/usb.ids"
                logger.lifecycle("Downloading usb.ids from $sourceUrl to ${destFile.absolutePath}")
                ant.invokeMethod("get", mapOf("src" to sourceUrl, "dest" to destFile))
                if (!destFile.exists()) {
                    logger.warn("Download finished but ${destFile.absolutePath} does not exist; copying placeholder")
                    destFile.parentFile.mkdirs()
                    copy {
                        from(project.file("usb.ids"))
                        into(destFile.parentFile)
                    }
                }
            }
        } catch (t: Throwable) {
            logger.warn("Failed to download/extract usb.ids: ${t.message}. Copying placeholder file instead.")
            destFile.parentFile.mkdirs()
            copy {
                from(project.file("usb.ids"))
                into(destFile.parentFile)
            }
        }
    }
}

// Add verification task for CI that ensures usb.ids was produced and contains the expected header
tasks.register("verifyUsbIds") {
    group = "verification"
    description = "Verify that usb.ids was produced and contains the expected header (for CI)"
    dependsOn(downloadUsbIdFile)

    doLast {
        val outFile = layout.buildDirectory.file("usb.ids").get().asFile
        if (!outFile.exists()) {
            throw GradleException("verifyUsbIds: expected file ${outFile.absolutePath} does not exist")
        }

        val firstNonBlank = outFile.useLines { seq -> seq.map { it.trim() }.firstOrNull { it.isNotEmpty() } ?: "" }
        if (firstNonBlank.isEmpty() || !firstNonBlank.startsWith("#")) {
            throw GradleException("verifyUsbIds: ${outFile.absolutePath} does not contain expected header; firstNonBlank='$firstNonBlank'")
        }

        logger.lifecycle("verifyUsbIds: ${outFile.absolutePath} looks valid; header='${firstNonBlank.take(80)}'")
    }
}
