package horus.commands.discord.defaults

import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import java.time.Instant
import kotlin.system.exitProcess

class SystemCommand : DiscordCommand("system", arrayOf("sys"), "System command", DiscordCategory.ADMIN) {
    private val pattern = Regex("^-([a-zA-Z0-9]|-[a-zA-Z0-9\\-]+=)(.+)$")

    override fun run(event: DiscordCommandEvent) {
        if (event.args.isNotEmpty()) {
            val options = event.args.filter { it.matches(pattern) }
            val arguments = event.args.filter { !it.matches(pattern) }
            val arg = arguments[0]
            val postArguments = arguments.joinToString(" ") { if (it == arg) "" else it }.split(" ")

            when (arg) {
                "shutdown", "kill" -> doShutdown(event, options, postArguments)
                "set" -> doSet(event, options, postArguments)
                else -> {
                    event.replay("Unknown parameter `${arguments[0]}`. *Throwing usage command.*").thenEmpty {
                        try {
                            callUsage(event)
                        } finally {
                            it.onComplete()
                        }
                    }.subscribe()
                }
            }
        } else {
            callUsage(event)
        }
    }

    private fun doShutdown(event: DiscordCommandEvent, options: List<String>, arguments: List<String>) {
        event.replay("Goodbye! :wave:").thenEmpty {
            it.onComplete()
            exitProcess(0)
        }.subscribe()
    }

    private fun doSet(event: DiscordCommandEvent, options: List<String>, arguments: List<String>) {
        event.replay {
            setEmbed {
                it.setColor(Colors.DANGER)

                it.setTitle("There is no to set in this command!")
                it.setDescription("Check back later!")

                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

    override fun callUsage(event: DiscordCommandEvent) {
        val suffix = "[shutdown|set]"
        event.replay {
            setEmbed {
                it.setColor(Colors.PRIMARY)
                it.setTitle("`${event.api.defaultPrefix}${this@SystemCommand.name} $suffix`")
                it.setDescription(this@SystemCommand.description!!)
                it.addField("Aliases", this@SystemCommand.alias.joinToString("\n") { "`${event.api.defaultPrefix + it} $suffix`" }, false)
                it.addField("Category", this@SystemCommand.category.value, false)
                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }
}