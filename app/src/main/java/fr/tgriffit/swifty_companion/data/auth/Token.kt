package fr.tgriffit.swifty_companion.data.auth

/**
 * Names SHOULD follow snake_case typo for Gson parsing
 */
data class Token(
    var access_token: String = "null",
    var token_type: String = "bearer",
    var expires_in: Number,
    var scope: String = "Public",
    var created_at : Int = 0
    ){
    override fun toString(): String {
        return "Token(access_token='$access_token', \n" +
                "token_type='$token_type', \n" +
                "expires_in=$expires_in, \n" +
                "scope='$scope', \n" +
                "created_at=$created_at)"
    }
}
