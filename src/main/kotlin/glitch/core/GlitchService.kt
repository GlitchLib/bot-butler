package glitch.core

import glitch.BotButler
import glitch.GlitchClient
import glitch.helix.GlitchHelix
import glitch.kraken.GlitchKraken
import glitch.kraken.`object`.enums.StreamType
import glitch.kraken.services.StreamService
import glitch.kraken.services.UserService
import glitch.pubsub.GlitchPubSub
import glitch.pubsub.Topic
import glitch.pubsub.events.StreamUpEvent
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder

class GlitchService(
        val client: GlitchClient,
        streams: Set<String>
) {
    val kraken = GlitchKraken.create(client)
    val helix = GlitchHelix.create(client)

    val pubSub = GlitchPubSub.builder(client)
            .apply {
                streams.forEach {
                    activateTopic(Topic.videoPlayback(it))
                }
            }.build()

    init {
        pubSub.connect().thenMany(pubSub.listenOn(StreamUpEvent::class.java))
                .subscribe {event ->
                    kraken.use(StreamService::class.java).zipWith(kraken.use(UserService::class.java)).flatMap {t ->
                        t.t2.getUsers(event.topic.suffix[0]).next().flatMap {u -> t.t1.getStreamByUser(u.id, StreamType.LIVE) }
                    }.subscribe { stream ->
                        val channel = BotButler.jda.getTextChannelById(BotButler.config.notification.streams)

                        channel.sendMessage(MessageBuilder().apply {
                            setContent("@everyone, **${stream.channel.username}** is start streaming! <${stream.channel.url}>")
                            setEmbed(EmbedBuilder().apply {
                                setAuthor(stream.channel.username, null, stream.channel.logo)
                                setTitle(stream.channel.title, stream.channel.url)
                                setTimestamp(event.data.serverTime)
                                addField("Game", stream.game, true)
                                addField("Viewers", stream.viewers.toString(), true)
                                setThumbnail(stream.channel.logo)
                                setImage(stream.preview.medium)
                                setFooter(null, stream.channel.logo)
                            }.build())
                        }.build()).queue()
                    }
                }
    }
}
