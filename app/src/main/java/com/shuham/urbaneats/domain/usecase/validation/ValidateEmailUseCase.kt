package com.shuham.urbaneats.domain.usecase.validation

import com.shuham.urbaneats.domain.model.ValidationResult

class ValidateEmailUseCase {

    // "invoke" allows us to call the class like a function: validateEmail(email)
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email can't be blank"
            )
        }
        // Simple regex for email validation
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        if (!email.matches(emailRegex)) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }
        return ValidationResult(successful = true)
    }
}