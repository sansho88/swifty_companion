package fr.tgriffit.swifty_companion.data.auth

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.Exception

open class AuthParams() {
    private val dotenv = dotenv { //path to .env file: app/src/main/assets/env
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
        ignoreIfMissing = true
    }
    val clientId: String = dotenv["UID"] //add your client id
    protected val clientSecret = dotenv["SECRET"] //add your client secret

    val redirectUri =
        "myapp://callback/"//add your redirect uri ( https://www.oauth.com/oauth2-servers/redirect-uris/redirect-uris-native-apps/ )
    protected val get42TokenUrl = "https://api.intra.42.fr/oauth/token" // url to get the token

}

class Request {
    val ME = "me"
    val USERS = "users"

    /**
     * Get an "array" of users matching perfectly the given login.
     * The array is size 1.
     * @param login=login of user searched
     */
    fun userByLogin(login: String): String {
        return "$USERS?filter[login]=$login"
    }

    /**
     * Get the current user.
     */
    fun me(): String {
        return ME
    }

    /**
     * Get the user with the given id.
     * @param id=id of user searched
     */
    fun userById(id: Int): String {
        return "$USERS/$id"
    }
}

/**
 * https://hirukarunathilaka.medium.com/token-based-authentication-rest-api-implementation-for-android-kotlin-apps-d2109b18eb36
 * https://api.intra.42.fr/apidoc/guides/getting_started
 */
class ApiService() : AuthParams() {
    constructor(token: Token?) : this() {
        this.token = token
    }

    protected var TAG = "ApiService"
    protected val requestApi42Url = "https://api.intra.42.fr/v2/"
    val request = Request()
    private var token: Token? = null
    private val executor = Executors.newSingleThreadExecutor() //for API calls!

    fun setToken(token: Token?) {
        if (token == null)
            Log.e(TAG, "[ApiService] The token to set is invalid")
        this.token = token
    }

    fun exchangeCodeForToken42(code: String): Token? {
        if (code.isEmpty()) return null
        TAG = "ApiService: exchangeCodeForToken42"

        val (request, response, result) = get42TokenUrl.httpPost(
            listOf(
                "grant_type" to "authorization_code",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to code,
                "redirect_uri" to redirectUri,
            )
        ).responseString()
        val parser = Gson()

        when (result) {

            is Result.Success -> {
                token = parser.fromJson<Token>(result.value, Token::class.java)
                Log.d(TAG, "Token created")
                return token
            }

            is Result.Failure -> {
                Log.e(TAG, "Impossible to get token from 42 with the code given")
                throw RuntimeException("\n[ApiService]The 42 app's API key should be renewed on the intranet.\n${result.error}")
            }
        }
    }

    fun getAbout(info: String?): String? {
        var result: String? = null
        if (info.isNullOrEmpty())
            return null
        executor.execute {
            result = callApi(info)
            Log.d(
                TAG,
                "GetAbout: result: $result. Is null? ${result == null}. Is empty? ${result == ""}"
            )
        }
        if (executor.awaitTermination(1, TimeUnit.SECONDS)) //attention au reseau...
            return result
        return result
    }

    private fun callApi(endPoint: String): String? {
        val fullUrl = requestApi42Url + endPoint

        if (token == null)
            throw RuntimeException("[CallApi] Invalid token")

        try {
            val (request, response, result) = fullUrl.httpGet()
                .header("Authorization", "${token!!.token_type} ${token!!.access_token}")
                .responseString()

            return when (result) {
                is Result.Success -> {
                    if (result.value == "[]")
                        ""
                    else
                        result.value
                }

                is Result.Failure -> {
                    Log.e(TAG, "[FAILURE] callApi: ${result.error.message}")
                    null
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, "callApi: $exception")
        }
        return ""
    }

}