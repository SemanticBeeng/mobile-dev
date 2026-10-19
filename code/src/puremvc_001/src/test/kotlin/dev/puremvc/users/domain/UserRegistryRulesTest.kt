package dev.puremvc.users.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserRegistryRulesTest {
    @Test
    fun `rejects duplicate ids`() {
        val first = UserFactory.create("same-id", UserDraft("Ada", "Lovelace", "ada@example.test"))
        val second = UserFactory.create("same-id", UserDraft("Grace", "Hopper", "grace@example.test"))

        assertFailsWith<IllegalArgumentException> {
            UserRegistryRules.requireUnique(listOf(first, second))
        }
    }

    @Test
    fun `rejects duplicate emails`() {
        val first = UserFactory.create("user-1", UserDraft("Ada", "Lovelace", "ada@example.test"))
        val second = UserFactory.create("user-2", UserDraft("Grace", "Hopper", "ada@example.test"))

        assertFailsWith<IllegalArgumentException> {
            UserRegistryRules.requireUnique(listOf(first, second))
        }
    }
}