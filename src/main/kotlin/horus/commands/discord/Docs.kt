package horus.commands.discord

import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import java.time.Instant

/**
 *
 * @author Damian Staszewski [damian@stachuofficial.tv]
 * @version %I%, %G%
 * @since 1.0
 */
class Docs : DiscordCommand("docs", arrayOf(), "Documentation Search", DiscordCategory.INFO) {
    override fun run(event: DiscordCommandEvent) {
        event.replay {
            setEmbed {
                it.setColor(Colors.INFO)
                it.setThumbnail("https://glitchlib.github.io/apple-touch-icon.png")

                it.addField("Wiki", "https://glitchlib.github.io/wiki", false)
                it.addField("Javadoc", "https://glitchlib.github.io/docs", false)
                it.addField("Javadoc Latest Release", "https://glitchlib.github.io/docs/latest/", false)
                it.addField("Tip", "You can search docs using `${event.api.defaultPrefix}$name [--version|-v] <search_query>`", false)

                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

}