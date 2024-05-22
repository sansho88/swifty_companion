package fr.tgriffit.swifty_companion.data.auth

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.DelicateCoroutinesApi
import java.lang.Exception
import java.util.UUID
import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class AuthParams {

    private val dotenv = dotenv { //path to .env file: app/src/main/assets/env
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
        ignoreIfMissing = true
    }
    val clientId: String = dotenv["UID"] //add your client id
    protected val clientSecret = dotenv["SECRET"] //add your client secret

    val redirectUri =
        "myapp://callback/"//add your redirect uri ( https://www.oauth.com/oauth2-servers/redirect-uris/redirect-uris-native-apps/ )
    protected val scope = "public"
    protected var state = "12345"
    protected val responseType = "code"
    protected val getAccess42ApiUrl = "https://api.intra.42.fr/oauth/authorize"
    protected val get42TokenUrl = "https://api.intra.42.fr/oauth/token" // url to get the token
    protected val grantType = "client_credentials"

    protected val googleUrl = "https://www.google.fr/" //for testing


}

/**
 * https://hirukarunathilaka.medium.com/token-based-authentication-rest-api-implementation-for-android-kotlin-apps-d2109b18eb36
 * https://api.intra.42.fr/apidoc/guides/getting_started
 */
class ApiService : AuthParams() {
    protected var TAG = "ApiService"

    private var token: String = "null"
    private var tokenType: String = "null"

    private val executor = Executors.newSingleThreadExecutor() //for API calls!
    private val handler = Handler(Looper.getMainLooper()) //for refresh UI with received data

    fun exchangeCodeForToken42(code: String) : Token? {
        if (code.isEmpty()) return null

        val (request, response, result) = get42TokenUrl.httpPost(
            listOf(
                "grant_type" to grantType,
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to code,
            )
        ).responseString()
        val parser = Gson()

        when (result){
            is Result.Success -> {
                Log.d(ContentValues.TAG, "Success  ${result.value}")

                return parser.fromJson<Token>(result.value, Token::class.java)
            }

            is Result.Failure -> {
                Log.e(ContentValues.TAG, "Impossible to get token from 42 with the code given")
                return null
            }

        }
    }

    private fun setPublicAuthToken() {

        try {
            val credentials = mapOf<String, String>(
                Pair<String, String>("client_id", clientId),
                Pair<String, String>("client_secret", clientSecret)
            )

            val (request, response, result) = get42TokenUrl.httpPost(
                listOf(
                    "grant_type" to grantType,
                    "client_id" to clientId,
                    "client_secret" to clientSecret
                )
            )
                .responseString()

            when (result) {
                is Result.Success -> {
                    var gson = Gson()
                    val tokenResultJson = gson.fromJson(result.value, Token::class.java)
                    Log.d(
                        TAG, "[SUCCESS] setPublicAuthToken:\n" +
                                "RESULT VALUUUUE:\n${result.value}"
                    )
                    token = tokenResultJson.access_token
                    tokenType = tokenResultJson.token_type
                    Log.d(TAG, "token $token")
                    Log.d(TAG, "token type $tokenType")
                }

                is Result.Failure -> {
                    if (response.statusCode == 401)
                        Log.e(
                            TAG, "=======================\n" +
                                    "setAuthToken: 401 Unauthorized\n" +
                                    "Check if the client_secret in the env file is still up-to-date" +
                                    "\n(app/src/main/assets/env)" +
                                    "\n======================="
                        )
                    else
                        Log.e(
                            TAG,
                            "setAuthToken: FAILED:\n" +
                                    "UID=$clientId\n" +
                                    "SECRET=$clientSecret\n" +
                                    "request => $request" +
                                    "\n Response => $response",
                        )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun request42AccessToUser(onResult: (String) -> Unit) {
        TAG += ": request42AccessToUser"
        GlobalScope.launch(Dispatchers.IO) {
            var finalResult = "Answer not received actually mon gars"

            val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
                    "client_id=$clientId&" +
                    "redirect_uri=$redirectUri&" +
                    "scope=$scope&" +
                    "response_type=code"
            try {
                state = UUID.randomUUID().toString()

                 val (request, response, result) = getAccess42ApiUrl.httpGet(
                listOf(
                    "client_id" to clientId,
                    "redirect_uri" to redirectUri,
                    "scope" to scope,
                    "state" to state,
                    "response_type" to responseType
                )
            )
                .responseString()

              //  val (request, response, result) = googleUrl.httpGet().responseString()

                when (result) {
                    is Result.Success -> {
                        Log.d(
                            TAG, "[SUCCESS] request42AccessToUser:\n" +
                                    "Ask for access page:${result.get()}"
                        )
                        // finalResult =  result.value
                        finalResult = result.get()
                        withContext(Dispatchers.Main) {
                            onResult(finalResult)
                        }

                    }

                    is Result.Failure -> {
                        if (response.statusCode == 401)
                            Log.e(
                                TAG, "=======================\n" +
                                        "request42AccessToUser: 401 Unauthorized\n" +
                                        "Check if the client_secret in the env file is still up-to-date" +
                                        "\n(app/src/main/assets/env)" +
                                        "\n======================="
                            )
                        else
                            Log.e(
                                TAG,
                                "request42AccessToUser: FAILED:\n" +
                                        "UID=$clientId\n" +
                                        "SECRET=$clientSecret\n" +
                                        "request => $request" +
                                        "\n Response => $response ====== ${result.error.exception}",
                            )
                        finalResult = result.error.message.toString()
                        withContext(Dispatchers.Main) {
                            onResult(finalResult)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /*init {
        executor.execute {
            setPublicAuthToken()
            request42AccessToUser { result -> Log.d(ContentValues.TAG, "Result: $result") }
            *//*callApi("https://api.intra.42.fr/v2/users", tokenType, token)*//*
            //callApi("https://api.intra.42.fr/v2/me", tokenType, token) //todo: oauth42 pour pouvoir la faire! (https://api.intra.42.fr/apidoc/guides/web_application_flow)
        }
    }*/
}