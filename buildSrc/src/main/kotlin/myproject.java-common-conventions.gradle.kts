plugins {
    java
}

version = "1.0.2"
group = "com.earthtoernie"

repositories {
    mavenCentral()
}

java {
    modularity.inferModulePath.set(true)
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
//        https://blog.gradle.org/java-toolchains
//        https://stackoverflow.com/questions/66450310/how-can-i-customize-a-kotlincompile-task-with-a-gradle-kotlin-buildsrc-plugin
    }
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
}
