package glitch

import net.dv8tion.jda.core.JDAInfo
import java.util.*

object Info {
    private val prop = Properties()
            .apply {
                load(ClassLoader.getSystemResourceAsStream("git.properties"))
            }

    val JDA = JDAInfo.VERSION
    val BOT_VERSION = prop.getProperty("application.version")
    val BOT_DESCRIPTION = prop.getProperty("application.description")
    val BOT_REVISION = prop.getProperty("git.commit.id.abbrev")
}