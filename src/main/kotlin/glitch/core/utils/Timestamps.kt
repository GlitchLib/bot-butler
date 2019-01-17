package glitch.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object Timestamps {
    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"))
    fun fromInstant(instant: Instant): String = formatter.format(instant)

    fun toInstant(date: String): Instant = Instant.from(formatter.parse(date))
}