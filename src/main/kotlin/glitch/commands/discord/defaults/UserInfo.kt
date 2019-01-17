package glitch.commands.discord.defaults

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import glitch.core.utils.Timestamps
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant

class UserInfo : Command("userinfo", arrayOf("me", "aboutme"), "User Info", Category.INFO) {
    override fun run(event: CommandEvent) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.INFO)
            setThumbnail(event.author.avatarUrl)
            setTitle("User Profile: ${event.author.asTag}")
            setDescription("""
                **Created:** ${Timestamps.fromInstant(event.author.creationTime.toInstant())}
            """.trimIndent())

            if (event.isJoinedGuild) {
                val member = event.member!!
                addField("In this server", """
                    **Joined: ** ${Timestamps.fromInstant(member.joinDate.toInstant())}
                    **Nickname: **${member.nickname}
                    ${if (member.roles.isNotEmpty()) {
                        "**Roles: ** ${ member.roles.joinToString(", ") { if (it.isMentionable) it.asMention else it.name } }"
                    } else ""}
                """.trimIndent(), false)
            }

            setTimestamp(Instant.now())
            setFooter("UID: ${event.author.id}", null)
        }.build())
    }
}