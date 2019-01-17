package glitch.commands.discord.api

import net.dv8tion.jda.core.Permission
import java.util.function.Predicate

abstract class Command(
    val name: String,
    val alias: Array<String> = arrayOf(),
    val description: String? = null,
    val category: Category
) : (CommandEvent) -> Unit {

    override fun invoke(event: CommandEvent) {
        if (isAccessible(event)) {
            run(event)
        } else {
            if (category.failMessage != null) {
                event.api.listener.onCommandException(this, event, CommandAccessException(category.failMessage))
            }
        }
    }

    abstract fun run(event: CommandEvent)

    internal open fun callUsage(event: CommandEvent) =
        event.api.listener.onCommandUsage(this, event)

    fun isCommandFor(command: String): Boolean =
            name.equals(command, ignoreCase = true) || (alias.isNotEmpty() && alias.any { it.equals(command, ignoreCase = true) })

    fun isAccessible(event: CommandEvent) = category.condition.test(event)

    enum class Category(
            private val value: String,
            val failMessage: String? = null,
            val condition: Predicate<CommandEvent> = Predicate { true }
    ) {
        ADMIN("Administration", "You must have a administrative privileges",
                Predicate {
                    when {
                        it.isOwner -> true
                        it.guild != null -> it.member!!.isOwner || it.member.hasPermission(Permission.ADMINISTRATOR)
                        else -> false
                    }
                }),
        CORE("Core"),
        SOCIAL("Social"),
        UTILS("Utilities"),
        INFO("Information");

        fun getName() = value
    }
}