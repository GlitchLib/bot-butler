package horus

import discord4j.core.util.VersionUtil
import java.util.*

object Info {
    private val prop = Properties()
            .apply {
                load(ClassLoader.getSystemResourceAsStream("git.properties"))
            }

    val DISCORD = VersionUtil.getProperties().getProperty(VersionUtil.APPLICATION_VERSION)
    val BOT_VERSION = prop.getProperty("application.version")
    val BOT_DESCRIPTION = prop.getProperty("application.description")
    val BOT_REVISION = prop.getProperty("git.commit.id.abbrev")
}