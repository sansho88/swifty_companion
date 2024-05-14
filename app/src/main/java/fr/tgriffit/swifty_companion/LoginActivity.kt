package fr.tgriffit.swifty_companion

import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.tgriffit.swifty_companion.data.auth.ApiService
import java.util.concurrent.Executors

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginButton : Button = findViewById(R.id.login_btn)

        val executor = Executors.newSingleThreadExecutor() //for API calls!
        ApiService()
        loginButton.setOnClickListener {

            try {
                executor.execute {
                    val connexionPage : WebView = findViewById(R.id.login_webView)
                    runOnUiThread {
                        connexionPage.loadData(
                            ApiService().request42AccessToUser(),
                            "text/html",
                            "UTF-8"
                        )
                    }
                   /* val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)*/
                }
            }
            catch (exception: Exception){
                Log.e(TAG, "[API ERROR] Something with the API process had a malfunction")
            }

        }


    }
}