package dev.puremvc.users.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserFactoryTest {
    @Test
    fun `trims names and normalizes email`() {
        val user = UserFactory.create("user-1", UserDraft(" Ada ", " Lovelace ", " ADA@Example.Test "))

        assertEquals("Ada Lovelace", user.fullName)
        assertEquals("ada@example.test", user.email)
        assertEquals(true, user.isActive)
    }

    @Test
    fun `rejects missing names and malformed emails`() {
        assertFailsWith<IllegalArgumentException> {
            UserFactory.create("user-1", UserDraft(" ", "Lovelace", "ada@example.test"))
        }
        assertFailsWith<IllegalArgumentException> {
            UserFactory.create("user-1", UserDraft("Ada", "Lovelace", "not-an-email"))
        }
    }

    @Test
    fun `update preserves identity status and creation time`() {
        val original = UserFactory.create("user-1", UserDraft("Ada", "Lovelace", "ada@example.test"))
            .copy(isActive = false)

        val updated = UserFactory.update(original, UserDraft("Augusta", "King", "augusta@example.test"))

        assertEquals(original.id, updated.id)
        assertEquals(original.isActive, updated.isActive)
        assertEquals(original.createdAt, updated.createdAt)
        assertEquals("Augusta King", updated.fullName)
    }
}