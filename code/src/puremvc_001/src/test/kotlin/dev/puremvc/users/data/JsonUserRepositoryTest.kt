package dev.puremvc.users.data

import dev.puremvc.users.domain.UserDraft
import dev.puremvc.users.domain.UserFactory
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class JsonUserRepositoryTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `round trips a versioned registry`() {
        val path = tempDir.resolve("nested/users.json")
        val repository = JsonUserRepository(path)
        val users = listOf(UserFactory.create("user-1", UserDraft("Ada", "Lovelace", "ada@example.test")))

        repository.save(users)

        assertEquals(users, repository.load())
        assertTrue(Files.readString(path).contains("\"schemaVersion\": 1"))
    }

    @Test
    fun `missing file loads as an empty registry`() {
        assertTrue(JsonUserRepository(tempDir.resolve("missing.json")).load().isEmpty())
    }

    @Test
    fun `malformed file is preserved when loading fails`() {
        val path = tempDir.resolve("users.json")
        val original = "{ this is not valid JSON"
        Files.writeString(path, original)

        assertFailsWith<UserRegistryReadException> { JsonUserRepository(path).load() }
        assertEquals(original, Files.readString(path))
    }
}