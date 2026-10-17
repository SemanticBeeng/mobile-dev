package dev.puremvc.users.data

import dev.puremvc.users.domain.User

interface UserRepository {
    fun load(): List<User>
    fun save(users: List<User>)
}