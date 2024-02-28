package fr.tgriffit.swifty_companion.data.auth

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import java.io.BufferedOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


/**
 * https://hirukarunathilaka.medium.com/token-based-authentication-rest-api-implementation-for-android-kotlin-apps-d2109b18eb36
 * https://api.intra.42.fr/apidoc/guides/getting_started
 */
class ApiService {
    private val dotenv = dotenv{
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
        ignoreIfMissing = true
    }
    private val clientId = dotenv["UID"] //add your client id
    private val clientSecret = dotenv["SECRET"] //add your client secret
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
    private fun setAuthToken() {

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
                        Log.d(TAG, "setAuthToken: RESULT VALUUUUE:\n${result.value}")
                        token = tokenResultJson.access_token
                        tokenType = tokenResultJson.token_type
                        Log.d(TAG, "token $token")
                        Log.d(TAG, "token type $tokenType")
                    }

                    is Result.Failure -> {
                        // handle error
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

    init {
        executor.execute {
            setAuthToken()
            callApi("https://api.intra.42.fr/v2/users", tokenType, token)
            //callApi("https://api.intra.42.fr/v2/me", tokenType, token) //todo: oauth42 pour pouvoir la faire! (https://api.intra.42.fr/apidoc/guides/web_application_flow)
        }
    }
}