package horus

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default
import java.io.File

class Cli(args: ArgParser) : Runnable {

    private val port: Int by args.storing("-p", "--port", help = "Server Port") { this.toInt() }.default(-1)
    private val discordToken: String by args.storing("--discord-token", help = "Discord Bot Token").default("")
    private val configuration by args.storing("-c", "--config", help = "Configuration file") { File(this) }
            .default(File("config.yml"))
            .addValidator {
                if (!this.value.name.matches(Regex("(.+)\\.y(a)?ml$"))) {
                    throw SystemExitException("Config file must be a YAML extension file", 255)
                }
            }


//    private val botToken: String
//    private val configName: File

    override fun run() {
        val horus = Horus(
                port,
                discordToken,
                configuration
        )

        horus.start()

        Runtime.getRuntime().addShutdownHook(Thread { horus.stop() })
    }
}
