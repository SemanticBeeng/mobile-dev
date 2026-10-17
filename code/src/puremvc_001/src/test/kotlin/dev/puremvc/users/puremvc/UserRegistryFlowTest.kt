package dev.puremvc.users.puremvc

import dev.puremvc.users.data.UserRepository
import dev.puremvc.users.domain.User
import dev.puremvc.users.domain.UserDraft
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRegistryFlowTest {
    @Test
    fun `mediator actions route through PureMVC commands and proxy`() {
        val repository = InMemoryUserRepository()
        val screen = RecordingScreen()

        UserRegistryApplication(repository, screen, "test-${UUID.randomUUID()}").use { application ->
            application.launch(seedDemo = false)
            screen.dispatch(UserAction.CreateUser(UserDraft("Grace", "Hopper", "grace@example.test")))

            assertEquals(1, repository.users.size)
            assertEquals("Grace Hopper", repository.users.single().fullName)
            assertEquals("Created Grace Hopper (${repository.users.single().id.take(8)}).", screen.lastUpdate?.message)

            screen.dispatch(UserAction.ListUsers)
            assertEquals(repository.users, screen.lastUpdate?.users)
        }

        assertTrue(screen.isUnbound)
    }

    private class InMemoryUserRepository : UserRepository {
        var users: List<User> = emptyList()
            private set

        override fun load(): List<User> = users

        override fun save(users: List<User>) {
            this.users = users.toList()
        }
    }

    private class RecordingScreen : UserRegistryScreen {
        private var actionHandler: ((UserAction) -> Unit)? = null
        var lastUpdate: ScreenUpdate? = null
            private set
        var isUnbound = false
            private set

        override fun bind(actionHandler: (UserAction) -> Unit) {
            this.actionHandler = actionHandler
        }

        override fun run() = Unit

        override fun present(update: ScreenUpdate) {
            lastUpdate = update
        }

        override fun unbind() {
            actionHandler = null
            isUnbound = true
        }

        fun dispatch(action: UserAction) {
            actionHandler?.invoke(action) ?: error("Screen is not bound.")
        }
    }
}