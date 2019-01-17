package glitch.commands.discord.api

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

data class CommandEvent(
        val api: Commander,
        val event: MessageReceivedEvent,
        val args: Array<String>
) {
    val isJoinedGuild = event.guild != null && event.member != null
    val isOwner = event.author.idLong == api.ownerId
    val member: Member? = event.member
    val author = event.author
    val guild: Guild? = event.guild
    val client = event.jda
    val channel = event.channel
    val message = event.message
    val content = message.contentDisplay

    fun send(message: MessageBuilder.() -> Unit) {
        channel.sendMessage(MessageBuilder().apply(message).build()).queue()
    }

    fun send(message: String, embed: EmbedBuilder.() -> Unit) = send {
        setEmbed(EmbedBuilder().apply(embed).build())
        setContent(message)
    }

    fun send(message: String) = send {
        setContent(message)
    }

    fun send(embed: MessageEmbed) = send {
        setEmbed(embed)
    }

    fun sendDm(message: MessageBuilder.() -> Unit) {
        author.openPrivateChannel().queue {
            it.sendMessage(MessageBuilder().apply(message).build()).queue()
        }
    }

    fun sendDm(message: String, embed: EmbedBuilder.() -> Unit) = sendDm {
        setEmbed(EmbedBuilder().apply(embed).build())
        setContent(message)
    }

    fun sendDm(message: String) = sendDm {
        setContent(message)
    }

    fun sendDm(embed: MessageEmbed) = sendDm {
        setEmbed(embed)
    }
}