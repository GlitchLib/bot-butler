package horus.commands.discord.defaults

import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import horus.core.utils.Timestamps
import java.time.Instant

class UserInfo : DiscordCommand("userinfo", arrayOf("me", "aboutme"), "User Info", DiscordCategory.INFO) {
    override fun run(event: DiscordCommandEvent) {
        event.author.flatMap { user ->
            event.replay {
                setEmbed {
                    it.setColor(Colors.INFO)
                    it.setThumbnail(user.avatarUrl)
                    it.setTitle("User Profile: ${user.username + "#" + user.discriminator}")
                    it.setDescription("""
                        **Created:** ${Timestamps.fromInstant(user.id.timestamp)}
                    """.trimIndent())
                    if (event.isJoinedGuild) {
                        val member = event.member!!
                        it.addField("In this server: **${member.guild.block()!!.name}**", """
                            **Joined:** ${Timestamps.fromInstant(member.joinTime)}
                            **Nickname:** ${member.nickname.orElse(user.username)}
                            ${if (member.roles.toIterable().toList().isNotEmpty()) {
                            "**Roles:** ${member.roles.toIterable().joinToString(", ") { if (it.isMentionable) it.mention else it.name }}"
                        } else ""}
                        """.trimIndent(), false)
                    }
                    it.setTimestamp(Instant.now())
                    it.setFooter("UID: ${user.id.asString()}", null)
                }
            }
        }.subscribe()
    }

}