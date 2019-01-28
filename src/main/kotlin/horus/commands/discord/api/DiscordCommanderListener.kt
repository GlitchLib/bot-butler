package horus.commands.discord.api

import discord4j.core.event.domain.message.MessageCreateEvent
import horus.Horus
import horus.core.utils.Colors
import java.time.Instant

class DiscordCommanderListener {
    fun onCommandUsage(event: DiscordCommandEvent, command: DiscordCommand) {
        LOG.debug("Calling Usage: ${event.api.defaultPrefix}${command.name}")

        event.replay {
            setEmbed {
                it.setColor(Colors.PRIMARY)
                it.setTitle("${event.api.defaultPrefix}${command.name}")
                it.setDescription(command.description ?: "[No description]")

                it.addField("Aliases", command.alias.joinToString("\n") { event.api.defaultPrefix + it }, false)

                it.addField("Category", command.category.value, false)

                it.setTimestamp(Instant.now())
            }
        }
    }

    fun onCommandExecute(event: DiscordCommandEvent, command: DiscordCommand) {
        LOG.debug("Execute command: ${event.api.defaultPrefix}${command.name}")
    }

    fun onCommandException(event: DiscordCommandEvent, command: DiscordCommand, throwable: Throwable) {
        LOG.error("Error on calling command: ${event.api.defaultPrefix}${command.name}", throwable)

        event.replay {
            setEmbed {
                it.setColor(Colors.DANGER)
                it.setTitle("ERROR: ${throwable::class.simpleName}")
                it.setDescription(throwable.localizedMessage)

                var cause = throwable.cause

                while (cause != null) {
                    it.addField("CAUSE: ${cause::class.simpleName}", cause.localizedMessage, false)
                    cause = cause.cause
                }
                it.setTimestamp(Instant.now())
            }
        }
    }

    fun onOrdinalMessage(horus: Horus, event: MessageCreateEvent) {}
}