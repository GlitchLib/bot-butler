package glitch.commands.discord.defaults


import glitch.Info
import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant

class BotInfo : Command("botinfo", arrayOf("bot"), "Bot Info", Category.INFO) {
    override fun run(event: CommandEvent) {

        event.client.asBot().applicationInfo.queue {appInfo ->
            event.send(EmbedBuilder().apply {
                val botUser = event.client.getUserById(appInfo.id)
                setThumbnail(botUser.avatarUrl)
                setColor(Colors.INFO)
                setTitle("Bot Details")
                setDescription("""
                    **Name:** ${botUser.asMention}
                    **Owner:** ${appInfo.owner.asMention}
                    **Server Count:** ${event.client.guilds.size}
                """.trimIndent())
                addField(Info.BOT_DESCRIPTION, "${Info.BOT_VERSION} rev.*${Info.BOT_REVISION}*", true)
                addField("JDA", Info.JDA, true)
                setTimestamp(Instant.now())
                setFooter("App ID: ${appInfo.id}", null)
            }.build())
        }
    }
}