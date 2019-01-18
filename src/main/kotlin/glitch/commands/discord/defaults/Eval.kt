package glitch.commands.discord.defaults

import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.ChannelType
import java.lang.System
import java.time.Instant
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext

class Eval : Command("eval", arrayOf(), "Evaluate your request", Category.UTILS) {
    private val engine = ScriptEngineManager().getEngineByName("kotlin")

    init {
        // omitting warning native filesystem for windows
        if (System.getProperty("os.name").startsWith("win", true)) {
            System.setProperty("idea.io.use.fallback", "true")
        }

        try {
            engine.eval("""
            import glitch.*
            import glitch.commands.discord.*
            import net.dv8tion.jda.core*
            import net.dv8tion.jda.core.entities.*
            import net.dv8tion.jda.core.entities.impl.*
            import net.dv8tion.jda.core.managers.*
            import net.dv8tion.jda.core.managers.impl.*
            import net.dv8tion.jda.core.utils.*
            """.trimIndent())
        } catch (_ : Throwable) {}
    }

    override fun run(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.send("Provide evaluation data")
        } else {
            val variableEvaluate = event.args.joinToString(" ").trim().let {
                if (it.matches(Regex("^`(.+)`$"))) {
                    return@let it.replace(Regex("^`(.+)`$"), "$1")
                }
                return@let it
            }

            try {
                val context = SimpleScriptContext()

                context.setAttribute("event", event, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("message", event.message, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("channel", event.channel, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("args", event.args, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("api", event.api, ScriptContext.ENGINE_SCOPE)
                context.setAttribute("jda", event.client, ScriptContext.ENGINE_SCOPE)

                if (event.event.isFromType(ChannelType.TEXT)) {
                    context.setAttribute("guild", event.guild, ScriptContext.ENGINE_SCOPE)
                    context.setAttribute("member", event.member, ScriptContext.ENGINE_SCOPE)
                }

                val out: Any? = engine.eval(variableEvaluate, context)

                event.send(EmbedBuilder().apply {
                    setTitle("Result")
                    setColor(Colors.PRIMARY)
                    setDescription("`${out.toString()}`")
                    setTimestamp(Instant.now())
                }.build())
            } catch (ex: Throwable) {
                event.api.listener.onCommandException(this, event, ex)
            }
        }
    }
}