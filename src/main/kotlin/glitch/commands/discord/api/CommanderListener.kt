package glitch.commands.discord.api

import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.time.Instant

abstract class CommanderListener {

    fun onCommandUsage(command: Command, event: CommandEvent) {
        LOG.debug("Calling Usage: ${event.api.defaultPrefix}${command.name}")

        event.send {
            setEmbed(EmbedBuilder().apply {
                setColor(Colors.PRIMARY)
                setTitle("`${event.api.defaultPrefix}${command.name}`")
                setDescription(command.description ?: "[No description]")

                addField("Aliases", command.alias.joinToString("\n") {"`${event.api.defaultPrefix + it}`"}, false)

                addField("Category", command.category.getName(), false)

                setTimestamp(Instant.now())
            }.build())
        }
    }

    fun onCommandExecute(command: Command, event: CommandEvent) {
        LOG.debug("Execute command: ${event.api.defaultPrefix}${command.name}")
    }

    fun onCommandException(command: Command, event: CommandEvent, throwable: Throwable) {
        LOG.error("Error on calling command: ${event.api.defaultPrefix}${command.name}", throwable)

        event.send {
            setEmbed(EmbedBuilder().apply {
                setColor(Colors.DANGER)
                setTitle("ERROR: ${throwable::class.simpleName}")
                setDescription(throwable.message)

                var cause = throwable.cause

                while (cause != null) {
                    addField("CAUSE: ${cause::class.simpleName}", cause.message, false)
                    cause = cause.cause
                }
                setTimestamp(Instant.now())
            }.build())
        }
    }

    fun onOrdinalMessage(event: MessageReceivedEvent) {}
}