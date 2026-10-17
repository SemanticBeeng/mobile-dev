package dev.puremvc.users.puremvc

import dev.puremvc.users.data.UserRepository
import org.puremvc.kotlin.multicore.interfaces.IFacade
import org.puremvc.kotlin.multicore.patterns.facade.Facade

class UserRegistryApplication(
    repository: UserRepository,
    private val screen: UserRegistryScreen,
    private val coreKey: String = DEFAULT_CORE_KEY
) : AutoCloseable {
    private val facade: IFacade = Facade.getInstance(coreKey) { key -> Facade(key) }

    init {
        facade.registerProxy(UserRegistryProxy(repository))
        facade.registerCommand(RegistryNotifications.LOAD_USERS_REQUESTED) { LoadUsersCommand() }
        facade.registerCommand(RegistryNotifications.SEARCH_USERS_REQUESTED) { SearchUsersCommand() }
        facade.registerCommand(RegistryNotifications.CREATE_USER_REQUESTED) { CreateUserCommand() }
        facade.registerCommand(RegistryNotifications.UPDATE_USER_REQUESTED) { UpdateUserCommand() }
        facade.registerCommand(RegistryNotifications.TOGGLE_USER_REQUESTED) { ToggleUserCommand() }
        facade.registerCommand(RegistryNotifications.REMOVE_USER_REQUESTED) { RemoveUserCommand() }
        facade.registerCommand(RegistryNotifications.SEED_DEMO_REQUESTED) { SeedDemoCommand() }
        facade.registerMediator(UserRegistryMediator(screen))
    }

    fun launch(seedDemo: Boolean) {
        facade.sendNotification(RegistryNotifications.LOAD_USERS_REQUESTED)
        if (seedDemo) facade.sendNotification(RegistryNotifications.SEED_DEMO_REQUESTED)
        screen.run()
    }

    override fun close() {
        facade.removeMediator(UserRegistryMediator.NAME)
        Facade.removeCore(coreKey)
    }

    companion object {
        const val DEFAULT_CORE_KEY = "user-registry"
    }
}