package com.shuham.urbaneats.domain.usecase.validation

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest {

    private lateinit var validateEmail: ValidateEmailUseCase

    @Before
    fun setUp() {
        validateEmail = ValidateEmailUseCase()
    }

    @Test
    fun `invoke with empty email returns error`() {
        val result = validateEmail("")
        assertFalse(result.successful)
        assertEquals("The email can't be blank", result.errorMessage)
    }

    @Test
    fun `invoke with invalid email format returns error`() {
        val result = validateEmail("shuham-email") // No @ or domain
        assertFalse(result.successful)
        assertEquals("That's not a valid email", result.errorMessage)
    }

    @Test
    fun `invoke with valid email returns success`() {
        val result = validateEmail("test@example.com")
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}