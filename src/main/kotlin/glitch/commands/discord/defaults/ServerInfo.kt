package glitch.commands.discord.defaults

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import glitch.core.utils.Timestamps
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import java.time.Instant

class ServerInfo : Command("serverinfo", arrayOf("server"), "Server Info", Category.INFO) {
    override fun run(event: CommandEvent) {
        if (event.isJoinedGuild) {
            val guild = event.guild!!
            val level = when (guild.verificationLevel) {
                Guild.VerificationLevel.NONE -> "unrestricted"
                Guild.VerificationLevel.LOW -> "must have verified email on account"
                Guild.VerificationLevel.MEDIUM -> "must be registered on Discord for longer than 5 minutes"
                Guild.VerificationLevel.HIGH -> "(╯°□°）╯︵ ┻━┻ - must be a member of the server for longer than 10 minutes"
                Guild.VerificationLevel.VERY_HIGH -> "┻━┻ミヽ(ಠ益ಠ)ﾉ彡┻━┻ - must have a verified phone number"
                else -> "[UNKNOWN]"
            }

            event.send(EmbedBuilder().apply {
                setColor(Colors.INFO)
                setTitle(guild.name)
                setThumbnail(guild.iconUrl)
                setDescription("""
                **Created:** ${Timestamps.fromInstant(guild.creationTime.toInstant())}
                **Owner:** ${guild.owner.asMention}
                **Verification Level:** $level
            """.trimIndent())
                setTimestamp(Instant.now())
                setFooter("ID: ${guild.id}", null)
            }.build())
        }
    }
}


