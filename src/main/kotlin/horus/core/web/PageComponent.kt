package horus.core.web

import io.ktor.routing.Routing

interface PageComponent : (Routing) -> Unit
