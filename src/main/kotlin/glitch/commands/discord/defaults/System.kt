package glitch.commands.discord.defaults

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant
import kotlin.system.exitProcess

class System : Command("system", arrayOf("sys"), "System command", Category.ADMIN) {
    private val pattern = Regex("^-([a-zA-Z0-9]|-[a-zA-Z0-9\\-]+=)(.+)$")

    override fun run(event: CommandEvent) {
        if (event.args.isNotEmpty()) {
            val options = event.args.filter { it.matches(pattern) }
            val arguments = event.args.filter { !it.matches(pattern) }
            val arg = arguments[0]
            val postArguments = arguments.joinToString(" ") { if (it == arg) "" else it }.split(" ")

            when (arg) {
                "shutdown", "kill" -> doShutdown(event, options, postArguments)
                "set" -> doSet(event, options, postArguments)
                else -> {
                    event.send("Unknown parameter `${arguments[0]}`. *Throwing usage command.*")
                    callUsage(event)
                }
            }
        } else {
            callUsage(event)
        }
    }

    private fun doShutdown(event: CommandEvent, options: List<String>, arguments: List<String>) {
        val force = options.any { it == "--force" }
        event.channel.sendMessage("Goodbye! :wave:").queue {
            if (force) {
                it.jda.shutdownNow()
            } else {
                event.client.shutdown()
            }

            exitProcess(0)
        }
    }

    private fun doSet(event: CommandEvent, options: List<String>, arguments: List<String>) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.DANGER)

            setTitle("There is no to set in this command!")
            setDescription("Check back later!")

            setTimestamp(Instant.now())
        }.build())
    }

    override fun callUsage(event: CommandEvent) {
        val suffix = "[shutdown|set]"
        event.send {
            setEmbed(EmbedBuilder().apply {
                setColor(Colors.PRIMARY)
                setTitle("`${event.api.defaultPrefix}${this@System.name} $suffix`")
                setDescription(this@System.description!!)

                addField("Aliases", this@System.alias.joinToString("\n") {"`${event.api.defaultPrefix + it} $suffix`"}, false)

                addField("Category", this@System.category.getName(), false)

                setTimestamp(Instant.now())
            }.build())
        }
    }
}