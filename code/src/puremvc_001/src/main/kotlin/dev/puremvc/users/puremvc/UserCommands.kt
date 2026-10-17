package dev.puremvc.users.puremvc

import dev.puremvc.users.domain.User
import org.puremvc.kotlin.multicore.interfaces.INotification
import org.puremvc.kotlin.multicore.patterns.command.SimpleCommand

abstract class UserRegistryCommand : SimpleCommand() {
    protected fun respond(action: UserRegistryProxy.() -> ScreenUpdate) {
        val update = try {
            registry().action()
        } catch (error: Exception) {
            ScreenUpdate(message = error.message ?: "The operation failed.", isError = true)
        }
        sendNotification(RegistryNotifications.SCREEN_UPDATE, update)
    }

    private fun registry(): UserRegistryProxy =
        facade.retrieveProxy(UserRegistryProxy.NAME) as? UserRegistryProxy
            ?: error("The user registry Proxy is not registered.")
}

class LoadUsersCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val users = load()
        ScreenUpdate(users = users, message = "Loaded ${users.size} user(s).")
    }
}

class SearchUsersCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val request = notification.body as? SearchUsersRequest
            ?: error("Search request was missing its query.")
        val results = search(request.query)
        ScreenUpdate(users = results, message = "Found ${results.size} matching user(s).")
    }
}

class CreateUserCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val draft = notification.body as? dev.puremvc.users.domain.UserDraft
            ?: error("Create request was missing user details.")
        val created = create(draft)
        ScreenUpdate(users = list(), message = "Created ${created.fullName} (${created.id.take(8)}).")
    }
}

class UpdateUserCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val request = notification.body as? UpdateUserRequest
            ?: error("Update request was missing user details.")
        val updated = update(request.idPrefix, request.draft)
        ScreenUpdate(users = list(), message = "Updated ${updated.fullName} (${updated.id.take(8)}).")
    }
}

class ToggleUserCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val request = notification.body as? UserIdRequest
            ?: error("Status request was missing a user ID.")
        val user = toggleActive(request.idPrefix)
        val status = if (user.isActive) "activated" else "deactivated"
        ScreenUpdate(users = list(), message = "${user.fullName} was $status.")
    }
}

class RemoveUserCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val request = notification.body as? UserIdRequest
            ?: error("Remove request was missing a user ID.")
        val removed = remove(request.idPrefix)
        ScreenUpdate(users = list(), message = "Removed ${removed.fullName} (${removed.id.take(8)}).")
    }
}

class SeedDemoCommand : UserRegistryCommand() {
    override fun execute(notification: INotification) = respond {
        val (users, seeded) = seedDemoIfEmpty()
        val message = if (seeded) "Added ${users.size} demo users." else "Demo data skipped; the registry is not empty."
        ScreenUpdate(users = users, message = message)
    }
}