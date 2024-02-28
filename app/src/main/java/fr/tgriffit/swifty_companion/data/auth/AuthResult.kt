package fr.tgriffit.swifty_companion.data.auth

/**
 * Names SHOULD follow snake_case typo for Gson parsing
 */
data class AuthResult(
    var access_token: String = "null",
    var token_type: String = "bearer",
    var expires_in: Number,
    var scope: String = "Public",
    var created_at : Int = 0
    )
