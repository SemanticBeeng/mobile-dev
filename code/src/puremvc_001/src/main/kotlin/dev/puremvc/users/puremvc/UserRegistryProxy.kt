package dev.puremvc.users.puremvc

import dev.puremvc.users.data.UserRepository
import dev.puremvc.users.domain.User
import dev.puremvc.users.domain.UserDraft
import dev.puremvc.users.domain.UserFactory
import dev.puremvc.users.domain.UserRegistryRules
import java.util.UUID
import org.puremvc.kotlin.multicore.patterns.proxy.Proxy

class UserRegistryProxy(private val repository: UserRepository) : Proxy(NAME) {
    private var users: List<User> = emptyList()
    private var loaded = false

    fun load(): List<User> {
        users = repository.load()
        loaded = true
        return users.toList()
    }

    fun list(): List<User> = currentUsers().toList()

    fun search(query: String): List<User> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) return list()
        return currentUsers().filter { user ->
            user.fullName.lowercase().contains(normalizedQuery) || user.email.contains(normalizedQuery)
        }
    }

    fun create(draft: UserDraft): User {
        val current = currentUsers()
        val user = UserFactory.create(UUID.randomUUID().toString(), draft)
        val updated = current + user
        UserRegistryRules.requireUnique(updated)
        persist(updated)
        return user
    }

    fun update(idPrefix: String, draft: UserDraft): User {
        val current = currentUsers()
        val existing = findByIdPrefix(idPrefix, current)
        val updated = UserFactory.update(existing, draft)
        val updatedUsers = current.map { if (it.id == existing.id) updated else it }
        UserRegistryRules.requireUnique(updatedUsers)
        persist(updatedUsers)
        return updated
    }

    fun toggleActive(idPrefix: String): User {
        val current = currentUsers()
        val existing = findByIdPrefix(idPrefix, current)
        val updated = existing.copy(isActive = !existing.isActive)
        persist(current.map { if (it.id == existing.id) updated else it })
        return updated
    }

    fun remove(idPrefix: String): User {
        val current = currentUsers()
        val existing = findByIdPrefix(idPrefix, current)
        persist(current.filterNot { it.id == existing.id })
        return existing
    }

    fun seedDemoIfEmpty(): Pair<List<User>, Boolean> {
        val current = currentUsers()
        if (current.isNotEmpty()) return current.toList() to false

        val demoUsers = listOf(
            UserFactory.create(UUID.randomUUID().toString(), UserDraft("Alex", "Morgan", "alex.morgan@example.test")),
            UserFactory.create(UUID.randomUUID().toString(), UserDraft("Sam", "Lee", "sam.lee@example.test"))
        )
        persist(demoUsers)
        return demoUsers to true
    }

    private fun currentUsers(): List<User> {
        if (!loaded) load()
        return users
    }

    private fun persist(updated: List<User>) {
        repository.save(updated)
        users = updated
        loaded = true
    }

    private fun findByIdPrefix(idPrefix: String, current: List<User>): User {
        val prefix = idPrefix.trim()
        require(prefix.isNotEmpty()) { "User ID is required." }
        val matches = current.filter { it.id.startsWith(prefix, ignoreCase = true) }
        return when (matches.size) {
            0 -> throw IllegalArgumentException("No user matches ID '$prefix'.")
            1 -> matches.single()
            else -> throw IllegalArgumentException("ID '$prefix' is ambiguous; enter more characters.")
        }
    }

    companion object {
        const val NAME = "UserRegistryProxy"
    }
}