package horus.commands.discord.defaults

import discord4j.core.`object`.entity.Channel
import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import java.time.Instant
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext

/**
 *
 * @author Damian Staszewski [damian@stachuofficial.tv]
 * @version %I%, %G%
 * @since 1.0
 */
class Eval : DiscordCommand("eval", arrayOf(), "Evaluate your request", DiscordCategory.UTILS) {
    private val engine = ScriptEngineManager().getEngineByName("kotlin")

    private val REGEX = Regex("^[`]{1,3}[\\\\n]?(.+[^:`])[`]{1,3}\$")

    init {
        // omitting warning native filesystem for windows
        if (java.lang.System.getProperty("os.name").startsWith("win", true)) {
            java.lang.System.setProperty("idea.io.use.fallback", "true")
        }

        try {
            engine.eval("""
            import horus.*
            import horus.core.*
            import horus.core.service.*
            import horus.core.utils.*
            import horus.core.web.*
            import horus.commands.discord.*
            import horus.commands.discord.api.*
            import horus.commands.discord.defaults.*

            import discord4j.core.*
            import discord4j.core.event.domain.message.*
            import discord4j.core.object.*
            import discord4j.core.object.audit.*
            import discord4j.core.object.data.*
            import discord4j.core.object.data.stored.*
            import discord4j.core.object.data.stored.embed.*
            import discord4j.core.object.entity.*
            import discord4j.core.object.presence.*
            import discord4j.core.object.reaction.*
            import discord4j.core.object.trait.*
            import discord4j.core.object.util.*
            import discord4j.core.spec.*
            import discord4j.core.util.*
            import discord4j.common.json.*

            import reactor.core.publisher.*
            """.trimIndent())
        } catch (_: Throwable) {
        }
    }

    override fun run(event: DiscordCommandEvent) {
        if (event.args.isEmpty()) {
            event.replay("***Provide evaluation data***")
        } else {
            val variableEvaluate = event.args.joinToString(" ").trim().let {
                if (it.matches(REGEX)) {
                    return@let it.replace(REGEX, "$1")
                }
                return@let it.trim()
            }

            try {
                val context = SimpleScriptContext()

                context.setAttribute("event", event, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("message", event.message, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("channel", event.channel, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("args", event.args, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("api", event.api, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("client", event.client, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("horus", event.api.horus, ScriptContext.ENGINE_SCOPE)

                if (event.channel.block()!!.type == Channel.Type.GUILD_TEXT) {
                    context.setAttribute("guild", event.guild, ScriptContext.ENGINE_SCOPE)
                    context.setAttribute("member", event.member, ScriptContext.ENGINE_SCOPE)
                }

                val out: Any? = engine.eval(variableEvaluate, context)

                event.replay {
                    setEmbed {
                        it.setTitle("Result")
                        it.setColor(Colors.PRIMARY)
                        it.setDescription("`${out.toString()}`")
                        it.setTimestamp(Instant.now())
                    }
                }.doOnError {
                    event.api.listener.onCommandException(event, this, it)
                }.subscribe()
            } catch (ex: Throwable) {
                event.api.listener.onCommandException(event, this, ex)
            }
        }
    }
}