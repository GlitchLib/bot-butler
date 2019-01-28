package horus.core

import com.fasterxml.jackson.annotation.JsonProperty

data class HorusConfig(
        @JsonProperty(required = true)
        val discord: DiscordConfig,
        val server: ServerConfig,
        var github: GithubConfig,
        var notification: NotificationsConfig
) {

    data class ServerConfig(
            var port: Int = 8080
    )

    data class DiscordConfig(
            @JsonProperty(required = true)
            var botToken: String,
            var defaultPrefix: String = "!",
            var ownerId: Long = -1L
    )

    data class GithubConfig(
            var token: String,
            var project: String = "GlitchLib/glitch"
    )

    data class NotificationsConfig(
            var streams: Long = 529400103870660619L,
            var releases: Long = 529400103870660619L,
            var greetings: Long = 529400103870660619L
    )
}