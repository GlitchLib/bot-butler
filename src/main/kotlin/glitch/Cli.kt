package glitch

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import glitch.commands.discord.Docs
import glitch.commands.discord.GitHub
import glitch.commands.discord.api.Commander
import glitch.commands.discord.api.registerDefaultCommands
import glitch.core.BotButlerConfig
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.hooks.AnnotatedEventManager
import java.io.File

class Cli : CliktCommand() {

    private val botToken by option("--discord-token", help = "Discord Bot Token", envvar = "DISCORD_BOT_TOKEN")
            .prompt("Specify Bot Token", hideInput = true, showDefault = false, requireConfirmation = false)

    private val githubToken by option("--github-token", help = "Github Token", envvar = "GITHUB_TOKEN")
            .prompt("Specify Github Token", hideInput = true, showDefault = false, requireConfirmation = false)

    private val configName by option("-c", "--config", help = "Configuration file")
            .default("config.yml")

    override fun run() {
        BotButler.config = configBuild(configName)
        BotButler.jda = JDABuilder(AccountType.BOT)
                .apply {
                    setToken(BotButler.config.discord.botToken)
                    setEventManager(AnnotatedEventManager())
                    addEventListener(Commander(
                            BotButler.config.discord.defaultPrefix,
                            BotButler.config.discord.ownerId
                    ).apply {
                        registerDefaultCommands()
                        registerCommands()
                    })
                    setEnableShutdownHook(true)
                    setGame(Game.listening("you, with `${BotButler.config.discord.defaultPrefix}`! | Type: `${BotButler.config.discord.defaultPrefix}help`"))
                }.build()
    }

    private fun Commander.registerCommands() {
        register(
                Docs(),
                GitHub()
        )
    }

    private fun configBuild(file: String): BotButlerConfig {
        val f = File(file)

        return if (f.exists()) {
            BotButler.yamlMapper.readValue(f)
        } else {
            BotButlerConfig(
                    BotButlerConfig.DiscordConfig(botToken),
                    BotButlerConfig.GithubConfig(githubToken),
                    BotButlerConfig.NotificationsConfig()
            )
        }.apply {
            if (discord.botToken != botToken) {
                discord.botToken = botToken
            }

            if (github.token != githubToken) {
                github.token = githubToken
            }

            BotButler.yamlMapper.writeValue(f, this)
        }
    }
}