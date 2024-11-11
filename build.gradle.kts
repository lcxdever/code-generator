plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    // 2024.2+: Java 21，主要更改下 gradle 版本
    // https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html#platformVersions
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.roc"
version = "1.16"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }

}

dependencies {
    intellijPlatform {
        val type = providers.gradleProperty("platformType")
        val version = providers.gradleProperty("platformVersion")

        create(type, version)

        bundledPlugin("com.intellij.java")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }

    testImplementation("junit:junit:4.13.2")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
