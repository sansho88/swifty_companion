package fr.tgriffit.swifty_companion.data.auth

import android.content.ContentValues
import android.util.Log
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.github.cdimascio.dotenv.dotenv
import java.lang.Exception


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

    var token: String? = "null"
    var tokenType: String? = "null"


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
                Log.d(ContentValues.TAG, "Call API to 42 Intra Failed") //fixme: happens too often
            }
        }
    }
    private fun setAuthToken() {
        try {
            val (request, response, result) = apigeeTokenUrl.httpPost(listOf(
                "grant_type" to grantType,
            ))
                .authentication().basic(clientId, clientSecret)
                .responseString()

            when (result) {
                is Result.Success -> {
                    var gson = Gson()
                    val tokenResultJson = gson.fromJson(result.value, AuthResult::class.java)
                    token = tokenResultJson!!.accessToken!!
                    tokenType = tokenResultJson.tokenType!!
                    Log.d(ContentValues.TAG, "token $token")
                    Log.d(ContentValues.TAG, "token type $tokenType")
                }
                is Result.Failure -> {
                    // handle error
                    Log.e(ContentValues.TAG, "setAuthToken: FAILED:\n" +
                            "UID=$clientId\n" +
                            "SECRET=$clientSecret\n" +
                            "$result", )
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    init {
        setAuthToken()
        callApi("https://api.intra.42.fr/v2/cursus", tokenType!!, token!!)
    }
}