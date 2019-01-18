package glitch.commands.discord.api

import glitch.BotButler
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.SubscribeEvent

class Commander(
        val defaultPrefix: String,
        val ownerId: Long,
        val listener: CommanderListener = object : CommanderListener() {}
) {
    private val commandList: MutableSet<Command> = mutableSetOf()
    private val aliases: MutableMap<String, String> = mutableMapOf()

    val commands: Collection<Command>
        get() = commandList

    fun register(vararg command: Command) {
        commandList.addAll(command)
    }

    fun unregister(command: String) {
        commandList.removeIf { it.isCommandFor(command) }
    }

    @SubscribeEvent
    fun onJoinEvent(event: GuildMemberJoinEvent) {
        val channel = event.guild.getTextChannelById(BotButler.config.notification.greetings)
        if (!event.user.isBot) { // Ignoring bots to receive message into #welcome channel
            channel.sendMessage("${event.user.asMention}, Welcome to the **${event.guild.name}**! Please take a look on ${event.guild.getTextChannelById(488287209791946762L).asMention} before starting interaction into this server.")
        }
    }

    @SubscribeEvent
    fun onMessage(event: MessageReceivedEvent) {
        val channels = arrayOf(488288036661231641L, 495155075858169857L, 491658576574808077L, 325552936203714562L)
        val messageContent = event.message.contentDisplay

        if (messageContent.startsWith(defaultPrefix) && !event.author.isBot) {
            if (channels.contains(event.channel.idLong)) {
                val command = messageContent.substring(defaultPrefix.length, if (messageContent.indexOf(' ') > 0) messageContent.indexOf(' ') else messageContent.length)
                var args = messageContent.substring(defaultPrefix.length + command.length).trim().split(' ')

                if (args[0].isBlank()) {
                    args = arrayListOf()
                }

                val commandEvent = CommandEvent(this, event, args.toTypedArray())

                commandList.forEach {
                    if (it.isCommandFor(command)) {
                        listener.onCommandExecute(it, commandEvent)
                        it.invoke(commandEvent)
                    }
                }
            }
        } else {
            listener.onOrdinalMessage(event)
        }
    }
}