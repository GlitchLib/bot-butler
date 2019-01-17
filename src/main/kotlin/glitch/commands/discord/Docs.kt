package glitch.commands.discord

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant

class Docs : Command("docs", arrayOf(), "Documentation Search", Category.INFO) {
    override fun run(event: CommandEvent) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.INFO)
            setThumbnail("https://glitchlib.github.io/apple-touch-icon.png")

            addField("Wiki", "https://glitchlib.github.io/wiki", false)
            addField("Javadoc", "https://glitchlib.github.io/docs", false)
            addField("Javadoc Latest Release", "https://glitchlib.github.io/docs/latest/", false)
            addField("Tip", "You can search docs using `${event.api.defaultPrefix}$name [--version|-v] <search_query>`", false)

            setTimestamp(Instant.now())
        }.build())
    }
}