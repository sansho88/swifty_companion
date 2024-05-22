package fr.tgriffit.swifty_companion.data.auth

import android.content.ContentValues
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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
    protected val get42TokenUrl = "https://api.intra.42.fr/oauth/token" // url to get the token

}

/**
 * https://hirukarunathilaka.medium.com/token-based-authentication-rest-api-implementation-for-android-kotlin-apps-d2109b18eb36
 * https://api.intra.42.fr/apidoc/guides/getting_started
 */
class ApiService : AuthParams() {
    protected var TAG = "ApiService"
    protected val requestApi42Url = "https://api.intra.42.fr/v2/"
    private var token: Token? = null
    private val executor = Executors.newSingleThreadExecutor() //for API calls!
    //private val handler = Handler(Looper.getMainLooper()) //for refresh UI with received data

    fun setToken(token: Token?) {
        if (token == null)
            Log.e(TAG, "[ApiService] The token to set is invalid")
        this.token = token
    }

    fun exchangeCodeForToken42(code: String) : Token? {
        if (code.isEmpty()) return null

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

        when (result){

            is Result.Success -> {
                token = parser.fromJson<Token>(result.value, Token::class.java)
                return token
            }

            is Result.Failure -> {
                Log.e(ContentValues.TAG, "Impossible to get token from 42 with the code given")
                return null
            }
        }
    }

    /*private fun setPublicAuthToken() {

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
                    token = tokenResultJson
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

    }*/

    fun getMe(): String{
        TAG += ": getMe"
        var result = ""
        executor.execute{
            result = callApi("me")
            executor.shutdown()
        }
        if (executor.awaitTermination(42, TimeUnit.SECONDS))
            return result
        return result
    }


    private fun callApi(endPoint: String): String {
        val fullUrl = requestApi42Url + endPoint

        if (token == null)
            throw RuntimeException("[CallApi] Invalid token")

            try{
                val (request, response, result) = fullUrl.httpGet()
                    .header("Authorization", "${token!!.token_type} ${token!!.access_token}")
                    .responseString()

                when(result){
                    is Result.Success -> {
                        return result.value
                    }
                    is Result.Failure -> {
                        Log.e(TAG, "[FAILURE] callApi: ${result.error.message}")
                    }
                }

            }catch (exception: Exception){
                Log.e(TAG, "callApi: $exception")
            }

        return ""
    }

  /*  @OptIn(DelicateCoroutinesApi::class)
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
    }*/


    /*init {
        executor.execute {
            setPublicAuthToken()
            request42AccessToUser { result -> Log.d(ContentValues.TAG, "Result: $result") }
            *//*callApi("https://api.intra.42.fr/v2/users", tokenType, token)*//*
            //callApi("https://api.intra.42.fr/v2/me", tokenType, token) //todo: oauth42 pour pouvoir la faire! (https://api.intra.42.fr/apidoc/guides/web_application_flow)
        }
    }*/
}