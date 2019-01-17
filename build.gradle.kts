import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gorylenko.GitProperties
import com.gorylenko.GitPropertiesPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*
import java.text.SimpleDateFormat

plugins {
    kotlin("jvm") version "1.3.11"
    id("com.github.ben-manes.versions") version "0.20.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("com.gorylenko.gradle-git-properties") version "2.0.0"
    application
}

application.mainClassName = "glitch.BotButler"

group = "io.glitchlib"
version = "0.1.0"
description = "Glitch Bot Butler"

repositories {
    jcenter()
//    mavenLocal()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/s1m0nw1/KtsRunner")
    maven("https://dl.bintray.com/stachu540/GlitchLib/")
    maven("https://dl.bintray.com/stachu540/Java/")
    maven("https://kotlin.bintray.com/kotlinx/")
}

dependencies{
    compile("ch.qos.logback:logback-classic:1.2.3")

    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))

    compile("de.swirtz:ktsRunner:0.0.7")

    // Config
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    compile("com.fasterxml.jackson.core:jackson-annotations:2.9.8")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")

    // HTTP
    compile("com.squareup.okhttp3:okhttp:3.12.1")
    compile("com.squareup.okhttp3:okcurl:3.12.1")

    compile("io.ktor:ktor-client-core:1.1.1")
    compile("io.ktor:ktor-client-okhttp:1.1.1")
    compile("io.ktor:ktor-client-json-jvm:1.1.1")
    compile("io.ktor:ktor-client-jackson:1.1.1")

    compile(enforcedPlatform("io.glitchlib:glitch-BOM:0.4.0"))
    compile("io.glitchlib:glitch-core")
    compile("io.glitchlib:glitch-pubsub")
    compile("io.glitchlib:glitch-kraken")
    compile("io.glitchlib:glitch-helix")

    compile("net.dv8tion:JDA:3.8.1_454")

    compile("com.github.ajalt:clikt:1.6.0")
}

val gitProperties: GitPropertiesPluginExtension by extensions

gitProperties.apply {
    keys = listOf(
        "git.branch",
        "git.commit.id",
        "git.commit.id.abbrev",
        "git.commit.id.describe"
    )
    dateFormatTimeZone = "GMT"
    customProperty("application.name", project.name)
    customProperty("application.version", project.version)
    customProperty("application.description", project.description)
}

tasks {
    withType<Jar> {
        manifest {
            attributes.apply {
                put("Manifest-Version", "1.0")
                put("Created-By", "Gradle ${gradle.gradleVersion} - JDK ${System.getProperty("java.specification.version")} (${System.getProperty("java.version")})")
                put("Implementation-Title", rootProject.name)
                put("Implementation-Version", project.version)
                put("Implementation-Date", SimpleDateFormat("MMM dd yyyy HH:mm:ss zzz", Locale.ENGLISH).apply { setTimeZone(TimeZone.getTimeZone("GMT")) }.format(Date()))
            }
        }
    }

    withType<ShadowJar> {
        archiveName = project.name + ".jar"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Wrapper> {
        gradleVersion = "5.1.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}