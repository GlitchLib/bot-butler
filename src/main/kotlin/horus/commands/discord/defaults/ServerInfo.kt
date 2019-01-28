package horus.commands.discord.defaults

import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.util.Image
import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import horus.core.utils.Timestamps
import java.time.Instant

class ServerInfo : DiscordCommand("serverinfo", arrayOf("server"), "Server Info", DiscordCategory.INFO) {
    override fun run(event: DiscordCommandEvent) {
        if (event.isJoinedGuild) {
            event.guild.zipWhen { it.owner }.flatMap { t ->
                val level = when (t.t1.verificationLevel) {
                    Guild.VerificationLevel.NONE -> "unrestricted"
                    Guild.VerificationLevel.LOW -> "must have verified email on account"
                    Guild.VerificationLevel.MEDIUM -> "must be registered on Discord for longer than 5 minutes"
                    Guild.VerificationLevel.HIGH -> "(╯°□°）╯︵ ┻━┻ - must be a member of the server for longer than 10 minutes"
                    Guild.VerificationLevel.VERY_HIGH -> "┻━┻ミヽ(ಠ益ಠ)ﾉ彡┻━┻ - must have a verified phone number"
                    else -> "[UNKNOWN]"
                }
                event.replay {
                    setEmbed {
                        it.setColor(Colors.INFO)
                        it.setTitle(t.t1.name)
                        t.t1.getIconUrl(Image.Format.PNG).ifPresent { i -> it.setThumbnail(i) }
                        it.setDescription("""
                            **Created:** ${Timestamps.fromInstant(t.t1.id.timestamp)}
                            **Owner:** ${t.t2.mention}
                            **Verification Level:** $level
                        """.trimIndent())
                        it.setTimestamp(Instant.now())
                        it.setFooter("ID: ${t.t1.id.asString()}", null)
                    }
                }
            }.subscribe()
        }
    }
}

