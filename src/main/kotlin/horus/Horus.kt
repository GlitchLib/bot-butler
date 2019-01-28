package horus

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import horus.commands.discord.api.DiscordCommandAPI
import horus.core.HorusConfig
import horus.core.web.ServerApplication
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Mono
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeUnit

class Horus(
        port: Int,
        discordToken: String,
//        twitchClient: GlitchClient,
//        twitchToken: UserCredential,
        private val configFile: File
) {
    private val LOG = LoggerFactory.getLogger(Horus::class.java)

    val configuration = ObjectMapper(YAMLFactory())
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerKotlinModule().readValue<HorusConfig>(configFile).apply {
                if (port > 0) {
                    this.server.port = port
                }
                if (discordToken.isNotBlank()) {
                    this.discord.botToken = discordToken
                }
            }

    val server = embeddedServer(Netty, port = configuration.server.port) { ServerApplication(this) }
    val discord = DiscordClientBuilder(configuration.discord.botToken).apply {
        eventProcessor = EmitterProcessor.create(false)
//        storeService = RedisStoreService() TODO: Redis Storage
    }.build()

    val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                registerModule(JavaTimeModule())
            }
        }
    }

    fun start() {
        server.start()
        discord.eventDispatcher.on(MessageCreateEvent::class.java)
                .subscribe(DiscordCommandAPI(this).apply {
                    registerCommands()
                })
        discord.login().block()
    }

    fun stop() = Mono.delay(Duration.ofSeconds(5))
            .map<Unit> {
                server.stop(1, it, TimeUnit.SECONDS)
            }.doOnSuccess {
                discord.logout()
                LOG.info("Storing configuration after shutdown")
                try {
                    ObjectMapper(YAMLFactory())
                            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                            .registerKotlinModule().writeValue(configFile, configuration)
                } catch (ex: IOException) {
                    LOG.error("Cannot store configuration!", ex)
                }
            }.block()
}