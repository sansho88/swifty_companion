package fr.tgriffit.swifty_companion.data.auth

data class AuthResult(
    var accessToken: String? = null,
    var tokenType: String? = null,
    var expiresIn: Number,
    var scope: String = "Public",
    var createdAt : Int = 0
    )
