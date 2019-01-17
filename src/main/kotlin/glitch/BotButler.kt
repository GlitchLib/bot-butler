package glitch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import glitch.core.BotButlerConfig
import glitch.core.GlitchService
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import net.dv8tion.jda.core.JDA
import org.slf4j.LoggerFactory

object BotButler {
    internal val LOG = LoggerFactory.getLogger(BotButler::class.java)

    val yamlMapper = ObjectMapper(YAMLFactory())
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerModule(JavaTimeModule())
            .registerKotlinModule()

    val jsonMapper = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerModule(JavaTimeModule())
            .registerKotlinModule()

    lateinit var config: BotButlerConfig
        internal set
    lateinit var jda: JDA
        internal set
    lateinit var glitch: GlitchService
        internal set

    val httpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                registerModule(JavaTimeModule())
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = Cli().main(args)
}