package horus.commands.discord

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import horus.commands.discord.api.DiscordCategory
import horus.commands.discord.api.DiscordCommand
import horus.commands.discord.api.DiscordCommandEvent
import horus.core.utils.Colors
import horus.core.utils.Timestamps
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

class GitHub : DiscordCommand("github", arrayOf("git"), "Getting a information about project.", DiscordCategory.INFO) {
    private val REPO = "https://api.github.com/repos/"
    private val USER = "https://api.github.com/users/"
    private val ORGS = "https://api.github.com/orgs/"

    override fun run(event: DiscordCommandEvent) {
        val git = formatArgs(if (event.args.isNotEmpty()) event.args[0] else event.api.horus.configuration.github.project)

        val httpClient = event.api.horus.client

        GlobalScope.launch {
            val response: Any = when {
                git.repo != null && !git.isOrg -> httpClient.get<GitRepository>(REPO + git.toSuffix()) {
                    accept(ContentType.parse("application/vnd.github.mercy-preview+json"))
                }
                git.isOrg -> httpClient.get<GitOrg>(ORGS + git.toSuffix())
                else -> httpClient.get<GitUser>(USER + git.toSuffix())
            }

            when (response) {
                is GitRepository -> doRepo(event, response)
                is GitOrg -> doOrg(event, response)
                is GitUser -> doUser(event, response)
            }
        }.start()
    }

    override fun callUsage(event: DiscordCommandEvent) {
        val suffix = "[o:<orgname>|username[/repo]]"

        event.replay {
            setEmbed {
                it.setColor(Colors.PRIMARY)
                it.setTitle("`${event.api.defaultPrefix}${this@GitHub.name} $suffix`")
                it.setDescription(this@GitHub.description!!)
                it.addField("Aliases", this@GitHub.alias.joinToString("\n") { "`${event.api.defaultPrefix + it} $suffix`" }, false)
                it.addField("Category", this@GitHub.category.value, false)
                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

    private fun doUser(event: DiscordCommandEvent, response: GitUser) {
        event.replay {
            setEmbed {
                it.setColor(Colors.INFO)
                it.setTitle("User information [${response.login}]")
                it.setUrl(response.htmlUrl)
                it.setThumbnail(response.avatarUrl)
                if (response.bio != null) {
                    it.setDescription(response.bio)
                }
                it.addField("Created", Timestamps.fromInstant(response.createdAt), true)
                it.addField("Updated", Timestamps.fromInstant(response.updatedAt), true)
                it.addField("Repositories", response.publicRepos.toString(), true)
                it.addField("Followers", response.followers.toString(), true)
                it.addField("Following", response.followers.toString(), true)
                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

    private fun doOrg(event: DiscordCommandEvent, response: GitOrg) {
        event.replay {
            setEmbed {
                it.setColor(Colors.INFO)
                it.setTitle("Organization info [${response.name}]${if (response.isVerified) " :white_check_mark:" else ""}")
                it.setUrl(response.htmlUrl)
                it.setThumbnail(response.avatarUrl)
                it.setDescription(response.description)
                it.addField("Created", Timestamps.fromInstant(response.createdAt), true)
                it.addField("Repositories", response.publicRepos.toString(), true)
                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

    private fun doRepo(event: DiscordCommandEvent, response: GitRepository) {
        event.replay {
            setEmbed {
                it.setColor(Colors.INFO)
                it.setTitle("Repository information [${response.fullName}]")
                it.setUrl(response.htmlUrl)
                it.setThumbnail(response.owner.avatarUrl)
                it.setDescription(response.description)
                it.addField("Created", Timestamps.fromInstant(response.createdAt), true)
                it.addField("Updated", Timestamps.fromInstant(response.updatedAt), true)
                it.addField("Last Pushed", Timestamps.fromInstant(response.pushedAt), true)
                it.addField("Licence", response.license.name, true)
                it.addField("Watches", response.watchersCount.toString(), true)
                it.addField("Starred", response.stargazersCount.toString(), true)
                it.addField("Forks", response.forksCount.toString(), true)
                it.setTimestamp(Instant.now())
            }
        }.subscribe()
    }

    private fun formatArgs(args: String): Git {
        val r = args.split('/', limit = 2)
        return Git(r[0].replace("o:", ""), if (r.size > 1) r[1] else null, r[0].startsWith("o:"))
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class GitUser(
            val avatarUrl: String,
            val bio: String?,
            val createdAt: Instant,
            val htmlUrl: String,
            val location: String?,
            val login: String,
            val name: String,
            val publicGists: Int,
            val publicRepos: Int,
            val updatedAt: Instant,
            val followers: Int,
            val following: Int
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class GitOrg(
            val blog: String?,
            val collaborators: Int,
            val avatarUrl: String,
            val company: String?,
            val createdAt: Instant,
            val description: String,
            val email: String,
            val followers: Int,
            val following: Int,
            val htmlUrl: String,
            val isVerified: Boolean,
            val location: String?,
            val login: String,
            val name: String,
            val publicGists: Int,
            val publicRepos: Int
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class GitRepository(
            val createdAt: Instant,
            val defaultBranch: String,
            val description: String,
            val fork: Boolean,
            val forksCount: Int,
            val fullName: String,
            val htmlUrl: String,
            val license: License,
            val name: String,
            val networkCount: Int,
            val openIssuesCount: Int,
            val owner: Owner,
            val parent: GitRepository?,
            @JsonProperty("private")
            val isPrivate: Boolean,
            val pushedAt: Instant,
            val size: Int,
            val stargazersCount: Int,
            val subscribersCount: Int,
            val topics: Collection<String>,
            val updatedAt: Instant,
            val watchersCount: Int
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Owner(
                val id: Long,
                val avatarUrl: String,
                val htmlUrl: String,
                val login: String,
                val type: String
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class License(
                val id: Long,
                val key: String,
                val name: String,
                val spdxId: String
        )
    }

    data class Git(
            val user: String,
            val repo: String?,
            val isOrg: Boolean = false
    ) {
        fun toSuffix() = user + if (repo != null) "/$repo" else ""
    }
}