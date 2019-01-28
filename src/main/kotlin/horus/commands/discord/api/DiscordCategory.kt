package horus.commands.discord.api

import discord4j.core.`object`.util.Permission

/**
 *
 * @author Damian Staszewski [damian@stachuofficial.tv]
 * @version %I%, %G%
 * @since 1.0
 */
enum class DiscordCategory(
        val value: String,
        val responseFailure: String? = null,
        val condition: (DiscordCommandEvent) -> Boolean = { true }
) {
    ADMIN("Administration", "You must have a administrative privileges",
            { e ->
                e.isOwner ||
                        e.guild.flatMap { it.owner.map { it.id == e.member!!.id } }.block() ?: false ||
                        e.member?.basePermissions?.map { it.contains(Permission.ADMINISTRATOR) }?.block() ?: false
            }
    ),
    CORE("Core"),
    SOCIAL("Social"),
    UTILS("Utilities"),
    INFO("Information")
}