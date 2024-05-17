package fr.tgriffit.swifty_companion

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.util.Log

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import clientId
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.auth.AuthParams
import redirectUri
import java.util.concurrent.Executors

class LoginActivity: AppCompatActivity() {
    var TAG = "LOGIN_ACTIVITY"

    val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        Log.d(TAG, "=====IT =>\n${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "=====\nonCreate: RESULT_OK\n========")
            val intent = result.data
            val code = intent?.getStringExtra("code")
            val token = intent?.getStringExtra("token")
            val error = intent?.getStringExtra("error")
            if (code != null)
                Log.d(TAG, "onCreate: code = $code")

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val loginButton : Button = findViewById(R.id.login_btn)
        val authParams = AuthParams()
       // val connexionPage : WebView = findViewById(R.id.login_webView)
        val executor = Executors.newSingleThreadExecutor() //for API calls!
        val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
                "client_id=${authParams.clientId}&" +
                "redirect_uri=${authParams.redirectUri}&" +
                "response_type=code"
        //connexionPage.settings.javaScriptEnabled = true
        //connexionPage.visibility = View.INVISIBLE
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
        val action = browserIntent.action
        val data = browserIntent.data



        try {
            loginButton.setOnClickListener {
                TAG += ": Login Button"

                if (browserIntent.resolveActivity(packageManager) != null) {
                    register.launch(browserIntent)
                } else {
                    Log.d(TAG, "Aucune application pour gérer cet intent")
                }



                //startActivityForResult(browserIntent, 5)
                //todo: https://www.branch.io/resources/blog/how-to-open-an-android-app-from-the-browser/
                // voir Grand Point 2 (tres interessant imo)
                Log.d(TAG, "onCreate: data = ${data.toString()}" +
                        "\n action = ${action.toString()}")

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
                *//* val intent = Intent(this, MainActivity::class.java)
                 startActivity(intent)*//*
            }*/
        }
        catch (exception: Exception){
            Log.e(TAG, "[API ERROR] Something with the API process had a malfunction")
        }

      /*  connexionPage.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()
            }

        }
        connexionPage.settings.domStorageEnabled = true
        connexionPage.settings.javaScriptCanOpenWindowsAutomatically = true*/


    }
}