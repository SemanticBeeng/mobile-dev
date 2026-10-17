package dev.puremvc.users.puremvc

import org.puremvc.kotlin.multicore.interfaces.INotification
import org.puremvc.kotlin.multicore.patterns.mediator.Mediator

interface UserRegistryScreen {
    fun bind(actionHandler: (UserAction) -> Unit)
    fun run()
    fun present(update: ScreenUpdate)
    fun unbind()
}

class UserRegistryMediator(private val screen: UserRegistryScreen) : Mediator(NAME, screen) {
    override fun listNotificationInterests(): Array<String> = arrayOf(RegistryNotifications.SCREEN_UPDATE)

    override fun onRegister() {
        screen.bind(::handleAction)
    }

    override fun handleNotification(notification: INotification) {
        val update = notification.body as? ScreenUpdate ?: return
        screen.present(update)
    }

    override fun onRemove() {
        screen.unbind()
    }

    private fun handleAction(action: UserAction) {
        when (action) {
            UserAction.ListUsers -> sendNotification(RegistryNotifications.LOAD_USERS_REQUESTED)
            is UserAction.SearchUsers -> sendNotification(
                RegistryNotifications.SEARCH_USERS_REQUESTED,
                SearchUsersRequest(action.query)
            )
            is UserAction.CreateUser -> sendNotification(RegistryNotifications.CREATE_USER_REQUESTED, action.draft)
            is UserAction.UpdateUser -> sendNotification(RegistryNotifications.UPDATE_USER_REQUESTED, action.request)
            is UserAction.ToggleUser -> sendNotification(RegistryNotifications.TOGGLE_USER_REQUESTED, action.request)
            is UserAction.RemoveUser -> sendNotification(RegistryNotifications.REMOVE_USER_REQUESTED, action.request)
        }
    }

    companion object {
        const val NAME = "UserRegistryMediator"
    }
}