package fr.tgriffit.swifty_companion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.tgriffit.swifty_companion.data.auth.AuthParams
import fr.tgriffit.swifty_companion.data.auth.Token

class LoginActivity: AppCompatActivity() {
    private var TAG = "LOGIN_ACTIVITY"
    private val authParams = AuthParams()
    private val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
            "client_id=${authParams.clientId}&" +
            "redirect_uri=${authParams.redirectUri}&" +
            "response_type=code"

    private val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
    lateinit var profileIntent : Intent
    private var code : String? = ""
    private var token : Token? = null
    private var error : String? = ""


    override fun onResume() {
        super.onResume()
        obtainsTokenWhenCodeReceived()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        askForPermsApi()
    }

    private fun askForPermsApi(){
        profileIntent = Intent(this, UserProfileActivity::class.java)
        val loginButton : Button = findViewById(R.id.login_btn)

        try {
            loginButton.setOnClickListener {
                if (browserIntent.resolveActivity(packageManager) != null) {
                    startActivity(browserIntent)
                } else
                    Log.d(TAG, "Aucune application pour gérer cet intent")
            }
        }
        catch (exception: Exception){
            Log.e(TAG, "[API ERROR] Something with the API process had a malfunction:\n" +
                    "$exception")
        }
    }
    private fun handleAuthRedirect(intent: Intent?) {
        Log.d(TAG, "handleAuthRedirect")
        intent?.data?.let { uri ->
             code = uri.getQueryParameter("code")
             error = uri.getQueryParameter("error")
            if (code != null) {
                browserIntent.putExtra("code", code)
            } else if (error != null) {
                Log.e(TAG, "Error during authorization: [$error]")
                Toast.makeText(this, "API permissions are needed", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.i(TAG, "No authorization code or error found")
            }

        }
    }

    private fun obtainsTokenWhenCodeReceived(){
        if (token != null)
            return
        handleAuthRedirect(intent)
        if (!code.isNullOrEmpty())
        {
            try {
                token = Token.createTokenFromCode(code)
                if (token != null){
                    profileIntent.putExtra("token", token)
                    startActivity(profileIntent)
                }
            }catch (exception: Exception){
                Log.e(TAG, "Something with the API process had a malfunction:\n$exception")
            }
        }
    }

}