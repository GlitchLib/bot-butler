package glitch.commands.discord

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import glitch.BotButler
import glitch.commands.discord.api.Command
import glitch.commands.discord.api.CommandEvent
import glitch.core.utils.Colors
import glitch.core.utils.Timestamps
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.core.EmbedBuilder
import java.time.Instant

class GitHub : Command("github", arrayOf("git"), "Getting a information about project.", Category.INFO) {
    private val REPO = "https://api.github.com/repos/"
    private val USER = "https://api.github.com/users/"
    private val ORGS = "https://api.github.com/orgs/"

    override fun run(event: CommandEvent) {
        val git = formatArgs(if (event.args.isNotEmpty())event.args[0] else BotButler.config.github.project)

        GlobalScope.launch {
            val response: Any = when {
                git.repo != null && !git.isOrg -> BotButler.httpClient.get<GitRepository>(REPO + git.toSuffix()) {
                    accept(ContentType.parse("application/vnd.github.mercy-preview+json"))
                }
                git.isOrg -> BotButler.httpClient.get<GitOrg>(ORGS + git.toSuffix())
                else -> BotButler.httpClient.get<GitUser>(USER + git.toSuffix())
            }

            when (response) {
                is GitRepository -> doRepo(event, response)
                is GitOrg -> doOrg(event, response)
                is GitUser -> doUser(event, response)
            }
        }.start()
    }

    override fun callUsage(event: CommandEvent) {
        val suffix = "[o:<orgname>|username[/repo]]"
        event.send {
            setEmbed(EmbedBuilder().apply {
                setColor(Colors.PRIMARY)
                setTitle("`${event.api.defaultPrefix}${this@GitHub.name} $suffix`")
                setDescription(this@GitHub.description!!)

                addField("Aliases", this@GitHub.alias.joinToString("\n") {"`${event.api.defaultPrefix + it} $suffix`"}, false)

                addField("Category", this@GitHub.category.getName(), false)

                setTimestamp(Instant.now())
            }.build())
        }
    }

    private fun doUser(event: CommandEvent, response: GitUser) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.INFO)
            setTitle("User information [${response.login}]", response.htmlUrl)
            setThumbnail(response.avatarUrl)
            setDescription(response.bio)
            addField("Created", Timestamps.fromInstant(response.createdAt), true)
            addField("Updated", Timestamps.fromInstant(response.updatedAt), true)
            addField("Repositories", response.publicRepos.toString(), true)
            addField("Followers", response.followers.toString(), true)
            addField("Following", response.followers.toString(), true)

            setTimestamp(Instant.now())
        }.build())
    }

    private fun doOrg(event: CommandEvent, response: GitOrg) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.INFO)
            setTitle("Organization info [${response.name}]${if (response.isVerified) " :white_check_mark:" else ""}", response.htmlUrl)
            setThumbnail(response.avatarUrl)
            setDescription(response.description)
            addField("Created", Timestamps.fromInstant(response.createdAt), true)
            addField("Repositories", response.publicRepos.toString(), true)

            setTimestamp(Instant.now())
        }.build())
    }

    private fun doRepo(event: CommandEvent, response: GitRepository) {
        event.send(EmbedBuilder().apply {
            setColor(Colors.INFO)
            setTitle("Repository information [${response.fullName}]", response.htmlUrl)
            setThumbnail(response.owner.avatarUrl)
            setDescription(response.description)
            addField("Created", Timestamps.fromInstant(response.createdAt), true)
            addField("Updated", Timestamps.fromInstant(response.updatedAt), true)
            addField("Last Pushed", Timestamps.fromInstant(response.pushedAt), true)
            addField("Licence", response.license.name, true)
            addField("Watches", response.watchersCount.toString(), true)
            addField("Starred", response.stargazersCount.toString(), true)
            addField("Forks", response.forksCount.toString(), true)

            setTimestamp(Instant.now())
        }.build())
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