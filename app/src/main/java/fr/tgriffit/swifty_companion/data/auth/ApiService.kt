package fr.tgriffit.swifty_companion.data.auth

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.gms.common.api.Response
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import okhttp3.internal.notify
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.Exception

open class AuthParams() {
    private val dotenv = dotenv { //path to .env file: app/src/main/assets/env
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
        ignoreIfMissing = true
    }
    val clientId: String = dotenv["UID"]
    protected val clientSecret = dotenv["SECRET"]


    val redirectUri =
        "myapp://callback/"//add your redirect uri ( https://www.oauth.com/oauth2-servers/redirect-uris/redirect-uris-native-apps/ )
    protected val get42TokenUrl = "https://api.intra.42.fr/oauth/token"

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
    var lastResponseApi: ResponseApi? = null

    fun setToken(token: Token?) {
        if (token == null)
            Log.e(TAG, "[ApiService] The token to set is invalid")
        this.token = token
    }

    fun exchangeCodeForToken42(code: String): Token? {
        if (code.isEmpty()) return null
        TAG = "ApiService: exchangeCodeForToken42"

        val (_, _, result) = get42TokenUrl.httpPost(
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

    fun getAbout(info: String?): ResponseApi {
        var result: ResponseApi = ResponseApi(code = 0, value = "")
        val executor = Executors.newSingleThreadExecutor() //for API calls!

        if (info.isNullOrEmpty())
            return ResponseApi(code = 404, value = "")
        executor.execute {
            result = callApi(info)
            executor.shutdown()
        }
        if (executor.awaitTermination(10, TimeUnit.SECONDS)) //attention au reseau...
        {
            lastResponseApi = result
            return result
        }
        lastResponseApi = result
        return result
    }

    var nbTries = 0
    private fun callApi(endPoint: String): ResponseApi {
        val fullUrl = requestApi42Url + endPoint

        if (token == null)
            throw RuntimeException("[CallApi] Invalid token\n[ApiService]=> ${toString()}")

        try {
            val (_, response, result) = fullUrl.httpGet()
                .header("Authorization", "${token!!.token_type} ${token!!.access_token}")
                .responseString()

            return when (result) {
                is Result.Success -> {
                    nbTries = 0
                    if (result.value == "[]")
                        ResponseApi(value = "")
                    else
                        ResponseApi(value = result.value)
                }

                is Result.Failure -> {
                    Log.e(
                        TAG, "[FAILURE] callApi: ${result.error.message}" +
                                "\n=>${response.responseMessage}"
                    )
                    if (response.statusCode == 401 && nbTries < 1) {
                        refreshToken()
                        callApi(endPoint)
                        ++nbTries
                    }
                    ResponseApi(code = response.statusCode, value = response.responseMessage)
                }
            }
        } catch (exception: Exception) {
            Log.e(TAG, "callApi: $exception")
        }
        nbTries = 0
        return ResponseApi(code = -1, value = "Unknown error")
    }

    override fun toString(): String {
        return "ApiService(TAG='$TAG', requestApi42Url='$requestApi42Url', request=$request, token=$token)"
    }

    private fun refreshToken(){
        val (_, _, result) = get42TokenUrl.httpPost(
            listOf(
                "grant_type" to "refresh_token",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "refresh_token" to token?.refresh_token,
            )
        ).responseString()
        val parser = Gson()

        when (result) {

            is Result.Success -> {
                this.token = parser.fromJson<Token>(result.value, Token::class.java)
                Log.d(TAG, "Token refreshed")
            }

            is Result.Failure -> {
                Log.e(TAG, "Impossible to refresh the token")
                throw RuntimeException("\n[ApiService]${result.error}")
            }
        }

    }

    fun logout() {
        token = null
        lastResponseApi = null
    }

    class ResponseApi(code: Int = 0, value: String) {
        var failure: Failure? = null
        var success: Success? = null

        class Failure(code: Int, message: String) {
            val code: Int = code
            val message: String = message
        }

        class Success(val result: String) {
        }

        init {
            if (code == 0)
                success = Success(value)
            else
                failure = Failure(code, value)
        }
    }
}