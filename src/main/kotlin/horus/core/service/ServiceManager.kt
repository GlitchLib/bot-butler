package horus.core.service

import org.slf4j.LoggerFactory
import java.net.UnknownServiceException

class ServiceManager {
    private val threads: MutableMap<IService, Boolean> = mutableMapOf()
    private val LOG = LoggerFactory.getLogger(ServiceManager::class.java)

    init {

    }

    fun listServices() = threads.keys.toSet()

    fun listActiveServices() = threads.entries.filter { it.value }.map { it.key }.toSet()


    fun <S : IService> getService(type: Class<S>) = listServices().firstOrNull { it::class.java == type }
            ?: throw UnknownServiceException("Unknown registered service \"${type.simpleName}\"")

    inline fun <reified S : IService> getSerivce() = getService(S::class.java)

    fun activate(type: Class<out IService>): Boolean {
        return try {
            val trueService = getService(type)
            trueService.start()
            threads.replace(trueService, true)
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun disable(type: Class<out IService>, force: Boolean = false): Boolean {
        return try {
            val trueService = getService(type)
            trueService.stop(force)
            threads.replace(trueService, false)
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun install(service: IService, activate: Boolean): Boolean {
        if (register(service)) {
            if (activate && activate(service::class.java)) {
                return true
            }
            return true
        } else return false
    }

    fun uninstall(type: Class<out IService>, force: Boolean = false): Boolean {
        return if (disable(type, force)) {
            unregister(type)
        } else false
    }

    private fun register(service: IService): Boolean {
        return if (!threads.containsKey(service)) {
            threads[service] = false
            true
        } else false
    }

    private fun unregister(type: Class<out IService>): Boolean {
        val service = getService(type)

        return if (threads.containsKey(service)) {
            threads.remove(service)
            true
        } else false
    }
}
