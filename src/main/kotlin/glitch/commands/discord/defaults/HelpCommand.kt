package glitch.commands.discord.defaults

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant
import java.util.*

class HelpCommand : Command("help", arrayOf("h", "commands"), "Show command list", Category.CORE) {
    override fun run(event: CommandEvent) {
        val api = event.api
        val commands = api.commands
        val defaultPrefix = api.defaultPrefix
        val args = event.args

        if (args.isNotEmpty()) {
            if (commands.any { it.isCommandFor(args[0]) }) {
                val cmd = commands.first { it.isCommandFor(args[0]) }
                if (cmd.isAccessible(event)) {
                    cmd.callUsage(event)
                } else {
                    if (cmd.category.failMessage != null) {
                        event.api.listener.onCommandException(cmd, event, IllegalAccessException(cmd.category.failMessage))
                    }
                }
            } else {
                event.send {
                    setEmbed(EmbedBuilder().apply {
                        setColor(Colors.DANGER)
                        setTitle("Command Not Found!")
                        setDescription("`$defaultPrefix${event.args.joinToString(" ")}` - is not exists")
                        setTimestamp(Instant.now())
                    }.build())
                }
            }
        } else {
            val categories = commands.map { it.category }.toSet().toMutableList()
                    .apply {
                        sortWith(Comparator { c1, c2 -> c1.name.compareTo(c2.name) })
                    }

            event.send {
                setEmbed(EmbedBuilder().apply {
                    setColor(Colors.SUCCESS)
                    setTitle("List of all commands")
                    categories.forEach { c ->
                        if (c.condition.test(event)) { // checking access to category
                            addField(c.getName(), commands.filter { it.category == c && it.isAccessible(event) }.joinToString("\n") {
                                "`${defaultPrefix + it.name}`${if (it.description != null) " - " + it.description else ""}"
                            }, false)
                        }
                    }
                    setTimestamp(Instant.now())
                }.build())
            }
        }
    }
}