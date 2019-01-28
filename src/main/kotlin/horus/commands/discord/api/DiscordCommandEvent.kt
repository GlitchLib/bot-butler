package horus.commands.discord.api

import discord4j.core.`object`.entity.Member
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec

data class DiscordCommandEvent(
        val api: DiscordCommandAPI,
        val event: MessageCreateEvent,
        val args: Array<String>
) {
    val isJoinedGuild = event.member.isPresent
    val isOwner = event.message.author.map { it.id == api.ownerId }.block()!!
    val member: Member? = event.member.orElse(null)
    val author = event.message.author
    val guild = event.guild
    val client = event.client
    val channel = event.message.channel
    val message = event.message
    val content: String? = message.content.orElse(null)

    fun replay(message: MessageCreateSpec.() -> Unit) = channel.flatMap { it.createMessage { it.apply(message) } }

    fun replay(message: String, embed: EmbedCreateSpec.() -> Unit) = replay {
        setEmbed { it.apply(embed) }
        setContent(message)
    }

    fun replay(message: String) = replay { setContent(message) }

    fun replayDm(message: MessageCreateSpec.() -> Unit) = author.flatMap { it.privateChannel.flatMap { it.createMessage { it.apply(message) } } }

    fun replayDm(message: String, embed: EmbedCreateSpec.() -> Unit) = replayDm {
        setEmbed { it.apply(embed) }
        setContent(message)
    }

    fun replayDm(message: String) = replayDm { setContent(message) }
}