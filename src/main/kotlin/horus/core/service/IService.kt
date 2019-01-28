package horus.core.service

abstract class IService {
    internal abstract fun start()
    internal abstract fun stop(force: Boolean = false)
    internal abstract fun reload()

    annotation class Default

    annotation class AutoLoad
}