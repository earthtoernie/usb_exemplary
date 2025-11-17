// Root build script: ensure Kotlin compile tasks target JVM 25 to match Java toolchain

subprojects {
    afterEvaluate {
        val kotlinCompileClassRaw = try {
            Class.forName("org.jetbrains.kotlin.gradle.tasks.KotlinCompile")
        } catch (e: Throwable) {
            null
        }

        if (kotlinCompileClassRaw != null) {
            @Suppress("UNCHECKED_CAST")
            val kotlinCompileClass = kotlinCompileClassRaw as Class<org.gradle.api.Task>
            val collection = tasks.withType(kotlinCompileClass)
            for (task in collection) {
                try {
                    val getKotlinOptions = kotlinCompileClassRaw.getMethod("getKotlinOptions")
                    val kotlinOptions = getKotlinOptions.invoke(task)
                    val setJvmTarget = kotlinOptions.javaClass.getMethod("setJvmTarget", String::class.java)
                    setJvmTarget.invoke(kotlinOptions, "25")
                    logger.lifecycle("Set kotlinOptions.jvmTarget=25 on task ${'$'}{task.path}")
                } catch (t: Throwable) {
                    logger.debug("Couldn't set jvmTarget on ${'$'}{task.path}: ${'$'}{t.message}")
                }
            }
        } else {
            logger.debug("KotlinCompile class not found; skipping jvmTarget configuration")
        }
    }
}
