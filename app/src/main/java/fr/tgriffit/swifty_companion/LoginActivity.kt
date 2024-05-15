package fr.tgriffit.swifty_companion

import android.content.ContentValues.TAG

import android.os.Bundle
import android.util.Log

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.tgriffit.swifty_companion.data.auth.ApiService
import java.util.concurrent.Executors

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginButton : Button = findViewById(R.id.login_btn)
        val connexionPage : WebView = findViewById(R.id.login_webView)
        val executor = Executors.newSingleThreadExecutor() //for API calls!
        //connexionPage.settings.javaScriptEnabled = true
        //connexionPage.visibility = View.INVISIBLE


        try {
            executor.execute {
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
                /* val intent = Intent(this, MainActivity::class.java)
                 startActivity(intent)*/
            }
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


        ApiService()
        loginButton.setOnClickListener {
           /* loginButton.visibility = View.INVISIBLE
            connexionPage.visibility = View.VISIBLE*/

        }


    }
}