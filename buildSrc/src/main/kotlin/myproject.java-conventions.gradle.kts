plugins {
    java
}

version = "1.0.2"
group = "com.earthtoernie"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
