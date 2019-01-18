package glitch.commands.discord.defaults

import com.sun.management.OperatingSystemMXBean
import glitch.Info
import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import java.lang.management.ManagementFactory
import java.time.Instant
import javax.swing.filechooser.FileSystemView

class BotInfo : Command("botinfo", arrayOf("bot"), "Bot Info", Category.INFO) {

    private val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)

    val os: String
        get() = java.lang.System.getProperty("os.name")

    val memory: (Boolean) -> String
        get() = { "${(osBean.totalPhysicalMemorySize - osBean.freePhysicalMemorySize).toProperSize(it)}/${osBean.totalPhysicalMemorySize.toProperSize(it)}" }

    val cpu: String
        get() = "${osBean.processCpuLoad.format(2)}%"

    val drive: (Boolean) -> String
        get() = { b ->
            val roots = FileSystemView.getFileSystemView().roots
            if (roots.size > 1) {
                roots.joinToString("\n", prefix = "\n") { "- $it - ${(it.totalSpace - it.freeSpace).toProperSize(b)}/${it.totalSpace.toProperSize(b)}" }
            } else {
                roots.joinToString { "${(it.totalSpace - it.freeSpace).toProperSize(b)}/${it.totalSpace.toProperSize(b)}" }
            }
        }

    override fun run(event: CommandEvent) {
        val bytes = event.args.any { it.equals("--bytes", true) }

        event.client.asBot().applicationInfo.queue { appInfo ->
            event.send(EmbedBuilder().apply {
                val botUser = event.client.getUserById(appInfo.id)
                setThumbnail(botUser.avatarUrl)
                setColor(Colors.INFO)
                setTitle("Bot Details")
                setDescription("""
                **Name:** ${botUser.asMention}
                **Server Count:** ${event.client.guilds.size}
            """.trimIndent())
                addField(Info.BOT_DESCRIPTION, "${Info.BOT_VERSION} rev.*${Info.BOT_REVISION}*", true)
                addField("JDA", Info.JDA, true)
                addField("Usage", """
                **OS:** $os
                **Memory:** ${memory(bytes)}
                **CPU:** $cpu
                **Drive:** ${drive(bytes)}
            """.trimIndent(), true)
                setTimestamp(Instant.now())
                setFooter("App ID: ${appInfo.id}", null)
            }.build())
        }
    }

    private fun Double.format(digits: Int) = String.format("%.${digits}f", this)

    private fun Long.toProperSize(bytes: Boolean): String {
        var doubled = this.toDouble()
        val realD = doubled
        var unit = "Bytes"

        if (!bytes) {
            while (doubled > 1024.0) {
                when (unit) {
                    "Bytes" -> unit = "kB"
                    "kB" -> unit = "MB"
                    "MB" -> unit = "GB"
                    "GB" -> unit = "TB"
                    "TB" -> unit = "PB"
                }
                doubled /= 1024
            }
        }

        return "${if (doubled % 1.0 == 0.0) doubled.format(0) else doubled.format(2)} $unit"
    }
}