package horus.commands.discord.api

import discord4j.core.`object`.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import horus.Horus
import horus.commands.discord.defaults.*
import java.util.function.Consumer

class DiscordCommandAPI(
        val horus: Horus
) : Consumer<MessageCreateEvent> {

    val defaultPrefix = horus.configuration.discord.defaultPrefix
    val ownerId = Snowflake.of(horus.configuration.discord.ownerId)
    val listener = DiscordCommanderListener()

    private val commandList: MutableSet<DiscordCommand> = LinkedHashSet()

    val commands: Collection<DiscordCommand>
        get() = commandList

    init {
        register(
                BotInfo(),
                Eval(),
                HelpCommand(),
                ServerInfo(),
                SystemCommand(),
                UserInfo()
        )
    }

    fun register(vararg command: DiscordCommand) {
        commandList.addAll(command.toSet())
    }

    fun unregister(vararg command: String) {
        command.forEach { c -> commandList.removeIf { it.isCommandFor(c) } }
    }

    override fun accept(event: MessageCreateEvent) {
        event.message.content.ifPresent {
            if (it.startsWith(defaultPrefix)) {
                val command = it.substring(defaultPrefix.length, if (it.indexOf(' ') > 0) it.indexOf(' ') else it.length)
                var args = it.substring(defaultPrefix.length + command.length).trim().split(' ')

                if (args[0].isBlank()) {
                    args = arrayListOf()
                }

                val commandEvent = DiscordCommandEvent(this, event, args.toTypedArray())

                commandList.forEach {
                    if (it.isCommandFor(command)) {
                        listener.onCommandExecute(commandEvent, it)
                        it.invoke(commandEvent)
                    }
                }
            } else {
                listener.onOrdinalMessage(horus, event)
            }
        }
    }
}