package horus.commands.discord.defaults

import com.sun.management.OperatingSystemMXBean
import horus.Info
import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import java.lang.management.ManagementFactory
import java.time.Instant
import javax.swing.filechooser.FileSystemView

class BotInfo : DiscordCommand("botinfo", arrayOf("bot"), "Bot Info", DiscordCategory.INFO) {

    private val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)

    val cpu: String
        get() = "${osBean.processCpuLoad.format(2)}%"

    val memory: (Boolean) -> String
        get() = {
            val size = (osBean.totalPhysicalMemorySize - osBean.freePhysicalMemorySize)
            val total = osBean.totalPhysicalMemorySize

            "${size.toProperSize(it)}/${total.toProperSize(it)} (${((size.toDouble() / total.toDouble()) * 100).format(2)}%)"
        }

    val drive: (Boolean) -> String
        get() = { b ->
            val roots = FileSystemView.getFileSystemView().roots
            if (roots.size > 1) {
                roots.joinToString("\n", prefix = "\n") { "- $it - ${(it.totalSpace - it.freeSpace).toProperSize(b)}/${it.totalSpace.toProperSize(b)}" }
            } else {
                roots.joinToString { "${(it.totalSpace - it.freeSpace).toProperSize(b)}/${it.totalSpace.toProperSize(b)}" }
            }
        }


    override fun run(event: DiscordCommandEvent) {
        val bytes = event.args.any { it.equals("--bytes", true) }

        event.client.applicationInfo.zipWhen { event.client.getUserById(it.id) }.flatMap {
            val app = it.t1
            val bot = it.t2
            event.replay {
                setEmbed {
                    it.setThumbnail(bot.avatarUrl)
                    it.setColor(Colors.INFO)
                    it.setDescription("""
                        **Name:** ${bot.mention}
                        **Server Count:** ${event.client.guilds.toIterable().toList().size}
                    """.trimIndent())
                    it.addField(Info.BOT_DESCRIPTION, "${Info.BOT_VERSION} rev.*${Info.BOT_REVISION}*", true)
                    it.addField("Discord4J", Info.DISCORD, true)
                    it.addField("Usage", """
                        **Memory:** ${memory(bytes)}
                        **CPU:** $cpu
                        **Drive:** ${drive(bytes)}
                    """.trimIndent(), true)
                    it.setTimestamp(Instant.now())
                    it.setFooter("App ID: ${app.id}", null)
                }
            }
        }.subscribe()
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