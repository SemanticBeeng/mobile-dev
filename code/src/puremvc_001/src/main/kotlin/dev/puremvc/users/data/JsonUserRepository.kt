package dev.puremvc.users.data

import dev.puremvc.users.domain.User
import dev.puremvc.users.domain.UserRegistryRules
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonUserRepository(path: Path) : UserRepository {
    private val file = path.toAbsolutePath().normalize()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun load(): List<User> {
        if (Files.notExists(file)) return emptyList()

        return try {
            val document = json.decodeFromString<UserDocument>(Files.readString(file))
            require(document.schemaVersion == CURRENT_SCHEMA_VERSION) {
                "Unsupported user registry schema version: ${document.schemaVersion}."
            }
            val users = document.users.map { it.toDomain() }
            UserRegistryRules.requireUnique(users)
            users
        } catch (error: Exception) {
            if (error is UserRegistryReadException) throw error
            throw UserRegistryReadException(
                "Could not read the user registry at '$file'. The file was left unchanged. " +
                    "Check that it contains valid schema version $CURRENT_SCHEMA_VERSION JSON.",
                error
            )
        }
    }

    override fun save(users: List<User>) {
        UserRegistryRules.requireUnique(users)
        val parent = file.parent ?: error("The user registry path must have a parent directory.")
        Files.createDirectories(parent)

        val document = UserDocument(
            schemaVersion = CURRENT_SCHEMA_VERSION,
            users = users.map { it.toRecord() }
        )
        val temporaryFile = Files.createTempFile(parent, "${file.fileName}.", ".tmp")
        try {
            Files.writeString(temporaryFile, json.encodeToString(document), StandardCharsets.UTF_8)
            try {
                Files.move(temporaryFile, file, ATOMIC_MOVE, REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporaryFile, file, REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporaryFile)
        }
    }

    private fun UserRecord.toDomain() = User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun User.toRecord() = UserRecord(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        isActive = isActive,
        createdAt = createdAt
    )

    @Serializable
    private data class UserDocument(
        val schemaVersion: Int,
        val users: List<UserRecord>
    )

    @Serializable
    private data class UserRecord(
        val id: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val isActive: Boolean,
        val createdAt: String
    )

    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}

class UserRegistryReadException(message: String, cause: Throwable) : IOException(message, cause)