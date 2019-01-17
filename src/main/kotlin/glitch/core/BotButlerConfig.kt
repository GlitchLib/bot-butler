package glitch.core

import com.fasterxml.jackson.annotation.JsonProperty

data class BotButlerConfig(
        @JsonProperty(required = true)
        var discord: DiscordConfig,
        var github: GithubConfig,
        var notification: NotificationsConfig
) {

    data class DiscordConfig(
            @JsonProperty(required = true)
            var botToken: String,
            var defaultPrefix: String = "g!",
            var ownerId: Long = 118692792301125635L
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