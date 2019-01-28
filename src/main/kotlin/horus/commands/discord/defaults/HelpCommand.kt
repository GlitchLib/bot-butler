package horus.commands.discord.defaults

import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import java.time.Instant
import java.util.*

class HelpCommand : DiscordCommand("help", arrayOf("h", "commands"), "Show command list", DiscordCategory.CORE) {
    override fun run(event: DiscordCommandEvent) {
        val api = event.api
        val commands = api.commands
        val defaultPrefix = api.defaultPrefix
        val args = event.args

        if (args.isNotEmpty()) {
            if (commands.any { it.isCommandFor(args[0]) }) {
                commands.first { it.isCommandFor(args[0]) }
                        .callUsage(event)
            } else {
                event.replay {
                    setEmbed {
                        it.setColor(Colors.DANGER)
                        it.setTitle("Command Not Found!")
                        it.setDescription("`${defaultPrefix + event.args[0]}`")
                        it.setTimestamp(Instant.now())
                    }
                }.subscribe()
            }
        } else {
            val categories = commands.map { it.category }.toSet().toMutableList()
                    .apply {
                        sortWith(Comparator { c1, c2 -> c1.name.compareTo(c2.name) })
                    }

            event.replay {
                setEmbed {
                    it.setColor(Colors.SUCCESS)
                    it.setTitle("List of all commands")
                    it.setDescription("For getting specific command info use `${defaultPrefix}${this@HelpCommand.name} <command_name>`")
                    categories.forEach { c ->
                        if (c.condition(event)) {
                            it.addField(c.value, commands.filter { it.category == c }.sorted().joinToString("\n") {
                                "${defaultPrefix + it.name}${if (it.description != null) " - " + it.description else ""}"
                            }, false)
                        }
                    }
                    it.setTimestamp(Instant.now())
                }
            }.subscribe()
        }
    }
}