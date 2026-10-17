package dev.puremvc.users.domain

import java.time.Instant
import java.util.Locale

private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

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
        require(id.isNotBlank()) { "User ID is required." }
        require(firstName.isNotBlank() && firstName == firstName.trim() && firstName.length <= 80) {
            "First name must be trimmed, nonblank, and 80 characters or fewer."
        }
        require(lastName.isNotBlank() && lastName == lastName.trim() && lastName.length <= 80) {
            "Last name must be trimmed, nonblank, and 80 characters or fewer."
        }
        require(email == email.lowercase(Locale.ROOT) && emailPattern.matches(email)) {
            "Email must be valid and normalized to lowercase."
        }
        require(runCatching { Instant.parse(createdAt) }.isSuccess) { "Creation time must be an ISO-8601 timestamp." }
    }

    val fullName: String
        get() = "$firstName $lastName"
}

object UserFactory {
    fun create(id: String, draft: UserDraft): User {
        require(id.isNotBlank()) { "User ID is required." }
        val firstName = requiredName(draft.firstName, "First name")
        val lastName = requiredName(draft.lastName, "Last name")
        val email = draft.email.trim().lowercase(Locale.ROOT)
        require(emailPattern.matches(email)) { "Enter a valid email address." }

        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            isActive = true,
            createdAt = Instant.now().toString()
        )
    }

    fun update(current: User, draft: UserDraft): User {
        val validated = create(current.id, draft)
        return validated.copy(isActive = current.isActive, createdAt = current.createdAt)
    }

    private fun requiredName(value: String, label: String): String {
        val trimmed = value.trim()
        require(trimmed.isNotEmpty()) { "$label is required." }
        require(trimmed.length <= 80) { "$label must be 80 characters or fewer." }
        return trimmed
    }
}