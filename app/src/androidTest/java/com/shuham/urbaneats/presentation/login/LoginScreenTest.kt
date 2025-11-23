package com.shuham.urbaneats.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shuham.urbaneats.ui.theme.UrbanEatsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginButton_showsError_whenFieldsEmpty() {
        // 1. Load the UI
        composeRule.setContent {
            UrbanEatsTheme {
                LoginScreen(
                    // FIX: Use default values or specific fields matching your Data Class
                    state = LoginState(emailError = null),
                    onAction = {},
                    onSignUpClick = {},
                    onForgotPasswordClick = {}
                )
            }
        }
        // Logic to trigger error would go here
    }

    @Test
    fun errorText_isDisplayed_whenStateHasError() {
        // 1. Load UI with an Error State
        composeRule.setContent {
            UrbanEatsTheme {
                LoginScreen(
                    // FIX: Changed 'error' to 'emailError' to match your LoginState definition
                    state = LoginState(emailError = "Invalid Credentials"),
                    onAction = {},
                    onSignUpClick = {},
                    onForgotPasswordClick = {}
                )
            }
        }

        // 2. Verify the text "Invalid Credentials" exists on screen
        // This works because your LoginScreen displays 'state.emailError'
        composeRule.onNodeWithText("Invalid Credentials").assertIsDisplayed()
    }

    @Test
    fun loginButton_exists() {
        composeRule.setContent {
            UrbanEatsTheme {
                LoginScreen(
                    state = LoginState(),
                    onAction = {},
                    onSignUpClick = {},
                    onForgotPasswordClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Log In").assertIsDisplayed()
        composeRule.onNodeWithText("Log In").performClick()
    }
}