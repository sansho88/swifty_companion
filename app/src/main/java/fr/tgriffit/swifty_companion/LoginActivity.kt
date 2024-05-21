package fr.tgriffit.swifty_companion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.AuthParams
import fr.tgriffit.swifty_companion.data.auth.Token
import java.util.concurrent.Executors

class LoginActivity: AppCompatActivity() {
    private var TAG = "LOGIN_ACTIVITY"
    private val authParams = AuthParams()
    private val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
            "client_id=${authParams.clientId}&" +
            "redirect_uri=${authParams.redirectUri}&" +
            "response_type=code"

    private val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
    private var code : String? = ""
    private var token : Token? = null
    private var error : String? = ""


    override fun onResume() {
        super.onResume()
        handleAuthRedirect(intent)
        Log.d(TAG, "onResume: code = $code")

        if (!code.isNullOrEmpty()) {
            val executor = Executors.newSingleThreadExecutor() //for API calls!
            executor.execute {
                token = ApiService().exchangeCodeForToken42(code!!)
                Log.d(TAG, "onResume: token = $token")

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val loginButton : Button = findViewById(R.id.login_btn)
       // val executor = Executors.newSingleThreadExecutor() //for API calls!

        try {
            loginButton.setOnClickListener {
                TAG += ": Login Button"

                if (browserIntent.resolveActivity(packageManager) != null) {
                    startActivity(browserIntent)
                } else {
                    Log.d(TAG, "Aucune application pour gérer cet intent")
                }

            }
            /*executor.execute {
//                    loginButton.visibility = View.INVISIBLE
                // connexionPage.visibility = View.VISIBLE

                runOnUiThread {
                    ApiService().request42AccessToUser{result ->
                        connexionPage.loadData(
                            result,
                            "text/html",
                            "UTF-8"
                        )
                    }
                }

            }*/
        }
        catch (exception: Exception){
            Log.e(TAG, "[API ERROR] Something with the API process had a malfunction")
        }
    }
    private fun handleAuthRedirect(intent: Intent?) {
        Log.d(TAG, "handleAuthRedirect")
        intent?.data?.let { uri ->
             code = uri.getQueryParameter("code")
             error = uri.getQueryParameter("error")
            if (code != null) {
                Log.d(TAG, "Authorization code received: $code")
                browserIntent.putExtra("code", code)

                // Échange le code d'autorisation contre un token ici
            } else if (error != null) {
                Log.d(TAG, "Error during authorization: $error")
                browserIntent.putExtra("error", error)
            }
            else {
                Log.e(TAG, "No authorization code or error found")
            }

        }
    }

    private fun exchangeCodeForAccessToken(code: String){
        if (code.isEmpty()){
            throw Exception("Code is empty")
        }

    }
}