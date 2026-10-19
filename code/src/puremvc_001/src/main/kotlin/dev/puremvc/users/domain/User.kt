package dev.puremvc.users.domain

import java.time.Instant
import java.util.Locale

private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

internal object UserValidation {
    fun requireId(id: String) {
        require(id.isNotBlank()) { "User ID is required." }
    }

    fun normalizeName(value: String): String = value.trim()

    fun requireName(value: String, label: String) {
        require(value.isNotBlank() && value == value.trim() && value.length <= 80) {
            "$label must be trimmed, nonblank, and 80 characters or fewer."
        }
    }

    fun normalizeEmail(value: String): String = value.trim().lowercase(Locale.ROOT)

    fun requireCanonicalEmail(value: String) {
        require(value == value.lowercase(Locale.ROOT) && value == value.trim() && emailPattern.matches(value)) {
            "Email must be valid and normalized to lowercase."
        }
    }
}

data class UserDraft(
    val firstName: String,
    val lastName: String,
    val email: String
)

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isActive: Boolean,
    val createdAt: String
) {
    init {
        UserValidation.requireId(id)
        UserValidation.requireName(firstName, "First name")
        UserValidation.requireName(lastName, "Last name")
        UserValidation.requireCanonicalEmail(email)
        require(runCatching { Instant.parse(createdAt) }.isSuccess) { "Creation time must be an ISO-8601 timestamp." }
    }

    val fullName: String
        get() = "$firstName $lastName"
}

object UserFactory {
    fun create(id: String, draft: UserDraft): User {
        val normalized = normalize(draft)

        return User(
            id = id,
            firstName = normalized.firstName,
            lastName = normalized.lastName,
            email = normalized.email,
            isActive = true,
            createdAt = Instant.now().toString()
        )
    }

    fun update(current: User, draft: UserDraft): User {
        val normalized = normalize(draft)
        return current.copy(
            firstName = normalized.firstName,
            lastName = normalized.lastName,
            email = normalized.email
        )
    }

    private fun normalize(draft: UserDraft) = NormalizedUserDraft(
        firstName = UserValidation.normalizeName(draft.firstName),
        lastName = UserValidation.normalizeName(draft.lastName),
        email = UserValidation.normalizeEmail(draft.email)
    )

    private data class NormalizedUserDraft(
        val firstName: String,
        val lastName: String,
        val email: String
    )
}

object UserRegistryRules {
    fun requireUnique(users: List<User>) {
        requireDistinct(users, User::id, "Registry contains duplicate user IDs.")
        requireDistinct(users, User::email, "Registry contains duplicate email addresses.")
    }

    private fun <T, K> requireDistinct(items: List<T>, keyOf: (T) -> K, message: String) {
        val keys = HashSet<K>(items.size)
        require(items.all { keys.add(keyOf(it)) }) { message }
    }
}