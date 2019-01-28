package horus

import com.xenomachina.argparser.ArgParser
import horus.commands.discord.api.DiscordCommandAPI
import horus.commands.discord.*

fun main(args: Array<String>) = ArgParser(args).parseInto(::Cli).run()

internal fun DiscordCommandAPI.registerCommands() {
    register(
            Docs(),
            GitHub()
    )
}