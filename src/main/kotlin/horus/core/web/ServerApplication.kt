package horus.core.web

import io.ktor.application.Application
import io.ktor.routing.routing

class ServerApplication(val app: Application) {
    fun registerPage(page: PageComponent) {
        app.routing {
            page(this)
        }
    }
}
