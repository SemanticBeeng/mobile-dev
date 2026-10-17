package dev.puremvc.users.puremvc

import dev.puremvc.users.domain.User
import dev.puremvc.users.domain.UserDraft

object RegistryNotifications {
    const val LOAD_USERS_REQUESTED = "registry/loadUsersRequested"
    const val SEARCH_USERS_REQUESTED = "registry/searchUsersRequested"
    const val CREATE_USER_REQUESTED = "registry/createUserRequested"
    const val UPDATE_USER_REQUESTED = "registry/updateUserRequested"
    const val TOGGLE_USER_REQUESTED = "registry/toggleUserRequested"
    const val REMOVE_USER_REQUESTED = "registry/removeUserRequested"
    const val SEED_DEMO_REQUESTED = "registry/seedDemoRequested"
    const val SCREEN_UPDATE = "registry/screenUpdate"
}

data class SearchUsersRequest(val query: String)
data class UpdateUserRequest(val idPrefix: String, val draft: UserDraft)
data class UserIdRequest(val idPrefix: String)

data class ScreenUpdate(
    val users: List<User>? = null,
    val message: String? = null,
    val isError: Boolean = false
)

sealed interface UserAction {
    data object ListUsers : UserAction
    data class SearchUsers(val query: String) : UserAction
    data class CreateUser(val draft: UserDraft) : UserAction
    data class UpdateUser(val request: UpdateUserRequest) : UserAction
    data class ToggleUser(val request: UserIdRequest) : UserAction
    data class RemoveUser(val request: UserIdRequest) : UserAction
}