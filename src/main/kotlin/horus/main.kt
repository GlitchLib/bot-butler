package horus

import com.xenomachina.argparser.ArgParser
import horus.commands.discord.Docs
import horus.commands.discord.GitHub
import horus.commands.discord.api.DiscordCommandAPI

fun main(args: Array<String>) = ArgParser(args).parseInto(::Cli).run()

internal fun DiscordCommandAPI.registerCommands() {
    register(
            Docs(),
            GitHub()
    )
}