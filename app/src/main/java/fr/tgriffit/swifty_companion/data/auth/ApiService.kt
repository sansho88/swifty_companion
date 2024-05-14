package fr.tgriffit.swifty_companion.data.auth

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import java.lang.Exception
import java.util.UUID
import java.util.concurrent.Executors


/**
 * https://hirukarunathilaka.medium.com/token-based-authentication-rest-api-implementation-for-android-kotlin-apps-d2109b18eb36
 * https://api.intra.42.fr/apidoc/guides/getting_started
 */
class ApiService {
    private val dotenv = dotenv{ //path to .env file: app/src/main/assets/env
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
        ignoreIfMissing = true
    }
    private val clientId = dotenv["UID"] //add your client id
    private val clientSecret = dotenv["SECRET"] //add your client secret
    private val redirectUri = "swifty://callback" //add your redirect uri ( https://www.oauth.com/oauth2-servers/redirect-uris/redirect-uris-native-apps/ )
    private val scope = "public"
    private var state = "12345"
    private val responseType = "code"
    private val getAccess42ApiUrl = "https://api.intra.42.fr/oauth/authorize"
    private val apigeeTokenUrl = "https://api.intra.42.fr/oauth/token" // url to get the token
    private val grantType = "client_credentials"

    private var token: String = "null"
    private var tokenType: String = "null"

    private val executor = Executors.newSingleThreadExecutor() //for API calls!
    private val handler = Handler(Looper.getMainLooper()) //for refresh UI with received data

    private fun callApi(apiEndpoint: String, tokenType: String,              token: String): Any {


            val (request, response, result) = apiEndpoint
                .httpGet()
                .header(Pair("Authorization", "$tokenType $token"))
                .responseString()

            return when (result) {
                is Result.Success -> {
                    Log.d(ContentValues.TAG, "Success  ${result.value}")
                }
                is Result.Failure -> {
                    Log.d(ContentValues.TAG, "Call API to 42 Intra Failed!\n=> Request=$request\nResult=>$result")
                }
            }

    }
    private fun setPublicAuthToken() {

            try {
                val credentials = mapOf<String, String>(
                    Pair<String, String>("client_id", clientId),
                    Pair<String, String>("client_secret", clientSecret)
                )

                val (request, response, result) = apigeeTokenUrl.httpPost(listOf(
                    "grant_type" to grantType,
                    "client_id" to clientId,
                    "client_secret" to clientSecret
                ))
                    .responseString()

                when (result) {
                    is Result.Success -> {
                        var gson = Gson()
                        val tokenResultJson = gson.fromJson(result.value, AuthResult::class.java)
                        Log.d(TAG, "[SUCCESS] setAuthToken:\n" +
                                "RESULT VALUUUUE:\n${result.value}")
                        token = tokenResultJson.access_token
                        tokenType = tokenResultJson.token_type
                        Log.d(TAG, "token $token")
                        Log.d(TAG, "token type $tokenType")
                    }

                    is Result.Failure -> {
                        if (response.statusCode == 401)
                            Log.e(TAG, "=======================\n" +
                                    "setAuthToken: 401 Unauthorized\n" +
                                    "Check if the client_secret in the env file is still up-to-date" +
                                    "\n(app/src/main/assets/env)" +
                                    "\n=======================")
                        else
                            Log.e(TAG, "setAuthToken: FAILED:\n" +
                                "UID=$clientId\n" +
                                "SECRET=$clientSecret\n" +
                                "request => $request" +
                                "\n Response => $response", )
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
            }

    }

    private fun request42AccessToUser(){

        val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "scope=$scope&" +
                "response_type=code"
        try {
            state = UUID.randomUUID().toString()

            val (request, response, result) = getAccess42ApiUrl.httpGet(listOf(
                "client_id" to clientId,
                "redirect_uri" to redirectUri,
                "scope" to scope,
                "state" to state,
                "response_type" to responseType
            ))
                .responseString()

            when (result) {
                is Result.Success -> {
                   Log.d(TAG, "[SUCCESS] request42AccessToUser:\n" +
                           "Ask for access page:${result.value}")
                }

                is Result.Failure -> {
                    if (response.statusCode == 401)
                        Log.e(TAG, "=======================\n" +
                                "request42AccessToUser: 401 Unauthorized\n" +
                                "Check if the client_secret in the env file is still up-to-date" +
                                "\n(app/src/main/assets/env)" +
                                "\n=======================")
                    else
                        Log.e(TAG, "request42AccessToUser: FAILED:\n" +
                                "UID=$clientId\n" +
                                "SECRET=$clientSecret\n" +
                                "request => $request" +
                                "\n Response => $response", )
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    init {
        executor.execute {
            setPublicAuthToken()
            request42AccessToUser()
            callApi("https://api.intra.42.fr/v2/users", tokenType, token)
            //callApi("https://api.intra.42.fr/v2/me", tokenType, token) //todo: oauth42 pour pouvoir la faire! (https://api.intra.42.fr/apidoc/guides/web_application_flow)
        }
    }
}