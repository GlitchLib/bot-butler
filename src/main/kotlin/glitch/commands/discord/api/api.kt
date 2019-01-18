package glitch.commands.discord.api

import glitch.commands.discord.defaults.*
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("chorus.commands.discord")


fun Commander.registerDefaultCommands() {
    register(
            HelpCommand(),
            System(),
            BotInfo(),
            ServerInfo(),
            UserInfo(),
            Eval()
    )
}
