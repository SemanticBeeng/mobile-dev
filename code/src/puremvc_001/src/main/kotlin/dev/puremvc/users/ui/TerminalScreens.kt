package dev.puremvc.users.ui

import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.input.*
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.*
import dev.puremvc.users.domain.UserDraft
import dev.puremvc.users.domain.User
import dev.puremvc.users.puremvc.ScreenUpdate
import dev.puremvc.users.puremvc.UserAction
import dev.puremvc.users.puremvc.UserIdRequest
import dev.puremvc.users.puremvc.UserRegistryScreen
import dev.puremvc.users.puremvc.UpdateUserRequest

abstract class TerminalUserRegistryScreen : UserRegistryScreen {
    private var actionHandler: ((UserAction) -> Unit)? = null

    override fun bind(actionHandler: (UserAction) -> Unit) {
        this.actionHandler = actionHandler
    }

    override fun unbind() {
        actionHandler = null
    }

    override fun run() {
        var running = true
        while (running) {
            showMenu()
            when (prompt("Select an option")) {
                "1" -> dispatch(UserAction.ListUsers)
                "2" -> dispatch(UserAction.SearchUsers(prompt("Name or email to search")))
                "3" -> dispatch(UserAction.CreateUser(readDraft()))
                "4" -> {
                    val id = prompt("User ID (first 8 characters are enough)")
                    dispatch(UserAction.UpdateUser(UpdateUserRequest(id, readDraft())))
                }
                "5" -> dispatch(UserAction.ToggleUser(UserIdRequest(prompt("User ID to activate/deactivate"))))
                "6" -> {
                    val id = prompt("User ID to remove")
                    if (prompt("Remove this user? [y/N]").equals("y", ignoreCase = true)) {
                        dispatch(UserAction.RemoveUser(UserIdRequest(id)))
                    } else {
                        present(ScreenUpdate(message = "Removal cancelled."))
                    }
                }
                "0" -> running = false
                else -> present(ScreenUpdate(message = "Choose an option from 0 to 6.", isError = true))
            }
        }
    }

    override fun present(update: ScreenUpdate) {
        display(update)
    }

    protected abstract fun prompt(label: String): String
    protected abstract fun showMenu()
    protected abstract fun display(update: ScreenUpdate)

    private fun readDraft() = UserDraft(
        firstName = prompt("First name"),
        lastName = prompt("Last name"),
        email = prompt("Email address")
    )

    private fun dispatch(action: UserAction) {
        actionHandler?.invoke(action)
    }
}

class KotterUserRegistryScreen : TerminalUserRegistryScreen() {
    override fun prompt(label: String): String {
        var answer = ""
        session {
            section {
                cyan { text(label) }
                text("> ")
                input()
            }.runUntilInputEntered {
                onInputEntered { answer = input }
            }
        }
        return answer
    }

    override fun showMenu() {
        session {
            section {
                cyan { textLine("PureMVC User Registry") }
                textLine("1  List users       2  Search users")
                textLine("3  Add user         4  Edit user")
                textLine("5  Toggle status    6  Remove user")
                textLine("0  Exit")
            }.run()
        }
    }

    override fun display(update: ScreenUpdate) {
        session {
            section {
                update.message?.let { message ->
                    if (update.isError) red { textLine(message) } else green { textLine(message) }
                }
                update.users?.let { users -> renderUsers(users) }
            }.run()
        }
    }
}

class PlainUserRegistryScreen : TerminalUserRegistryScreen() {
    override fun prompt(label: String): String {
        print("$label> ")
        return readlnOrNull().orEmpty()
    }

    override fun showMenu() {
        println("\nPureMVC User Registry")
        println("1  List users       2  Search users")
        println("3  Add user         4  Edit user")
        println("5  Toggle status    6  Remove user")
        println("0  Exit")
    }

    override fun display(update: ScreenUpdate) {
        update.message?.let { message ->
            val prefix = if (update.isError) "Error: " else ""
            println("$prefix$message")
        }
        update.users?.let(::renderPlainUsers)
    }
}

private fun MainRenderScope.renderUsers(users: List<User>) {
    if (users.isEmpty()) {
        textLine("No users found.")
        return
    }

    textLine("ID       NAME                           EMAIL                            STATUS")
    textLine("-------  -----------------------------  -------------------------------  --------")
    users.forEach { user ->
        val name = user.fullName.take(29).padEnd(29)
        val email = user.email.take(31).padEnd(31)
        val status = if (user.isActive) "Active" else "Inactive"
        textLine("${user.id.take(8)}  $name  $email  $status")
    }
}

private fun renderPlainUsers(users: List<User>) {
    if (users.isEmpty()) {
        println("No users found.")
        return
    }

    println("ID       NAME                           EMAIL                            STATUS")
    println("-------  -----------------------------  -------------------------------  --------")
    users.forEach { user ->
        val name = user.fullName.take(29).padEnd(29)
        val email = user.email.take(31).padEnd(31)
        val status = if (user.isActive) "Active" else "Inactive"
        println("${user.id.take(8)}  $name  $email  $status")
    }
}