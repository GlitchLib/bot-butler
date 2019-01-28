import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gorylenko.GitPropertiesPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.*

plugins {
    kotlin("jvm") version "1.3.11"
    id("com.github.ben-manes.versions") version "0.20.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("com.gorylenko.gradle-git-properties") version "2.0.0"
    application
}

application.mainClassName = "horus.MainKt"

group = "io.horusproject"
version = "0.1.0"


repositories {
    jcenter()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://kotlin.bintray.com/kotlinx/")
    maven("https://dl.bintray.com/stachu540/GlitchLib/")
    maven("https://dl.bintray.com/stachu540/Java/")
}

dependencies {
    compile("ch.qos.logback:logback-classic:1.2.3")

    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))

    // Config
    compile(enforcedPlatform("com.fasterxml.jackson:jackson-bom:2.9.8"))
    compile("com.fasterxml.jackson.core:jackson-databind")
    compile("com.fasterxml.jackson.core:jackson-annotations")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    // Ktor
    compile("io.ktor:ktor-server-netty:1.1.2")
    compile("io.ktor:ktor-client-okhttp:1.1.2")
    compile("io.ktor:ktor-html-builder:1.1.2")
    compile("io.ktor:ktor-client-core:1.1.2")
    compile("io.ktor:ktor-client-json-jvm:1.1.2")
    compile("io.ktor:ktor-client-jackson:1.1.2")
    compile("io.ktor:ktor-jackson:1.1.2")
    compile("io.ktor:ktor-webjars:1.1.2")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.1.1")

    // HTTP
    compile("com.squareup.okhttp3:okhttp:3.12.1")
    compile("com.squareup.okhttp3:logging-interceptor:3.12.1")

    // Discord
    compile(enforcedPlatform("com.discord4j:bom:3.0.0.M1"))
    compile("com.discord4j:discord4j-core")
    compile("com.discord4j:stores-api")

    // Twitch
    // TODO: remove bugs - https://discordapp.com/channels/488285226452385792/488287498020323339/538438219864473620
//    compile(enforcedPlatform("io.glitchlib:glitch-BOM:0.4.0"))
//    compile("io.glitchlib:glitch-core")
//    compile("io.glitchlib:glitch-kraken")
//    compile("io.glitchlib:glitch-helix")
//    compile("io.glitchlib:glitch-pubsub")
//    compile("io.glitchlib:glitch-chat")

    compile("com.xenomachina:kotlin-argparser:2.0.7")
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
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    withType<Wrapper> {
        gradleVersion = "5.1.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}