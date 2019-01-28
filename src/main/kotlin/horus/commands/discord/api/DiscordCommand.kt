package horus.commands.discord.api

/**
 *
 * @author Damian Staszewski [damian@stachuofficial.tv]
 * @version %I%, %G%
 * @since 1.0
 */
abstract class DiscordCommand(
        val name: String,
        val alias: Array<String> = arrayOf(),
        val description: String? = null,
        val category: DiscordCategory
) : Comparable<DiscordCommand> {
    override fun compareTo(other: DiscordCommand) = this.name.compareTo(other.name)

    internal open fun callUsage(event: DiscordCommandEvent) =
            event.api.listener.onCommandUsage(event, this)

    fun isCommandFor(command: String): Boolean =
            name.equals(command, ignoreCase = true) || (alias.isNotEmpty() && alias.any { it.equals(command, ignoreCase = true) })


    fun invoke(event: DiscordCommandEvent) {
        if (category.condition(event)) {
            run(event)
        } else {
            if (category.responseFailure != null) {
                event.api.listener.onCommandException(event, this, CommandAccessException(category.responseFailure))
            }
        }
    }

    abstract fun run(event: DiscordCommandEvent)
}