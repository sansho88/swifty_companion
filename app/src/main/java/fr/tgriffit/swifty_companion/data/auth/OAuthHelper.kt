import android.accounts.AccountManagerCallback
import android.accounts.AccountManager
import android.accounts.AccountManagerFuture
import android.os.Bundle
import com.github.kittinunf.fuel.httpPost
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route


/**
 * Docu: https://square.github.io/okhttp/
 */
class OAuth2Authenticator(private val tokenProvider: TokenProvider) : Authenticator {
//todo: A TESTER, puis continuer implementation avec lecture env vars (surtout pour les secrets)


// Redirigez l'utilisateur vers l'URL d'autorisation
// (Utilisez l'intent pour ouvrir l'URL dans un navigateur ou un WebView)

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = tokenProvider.getAccessToken()

        // Si l'accessToken est null, cela signifie que l'utilisateur n'est pas authentifié
        // et vous pouvez rediriger l'utilisateur vers la page de connexion

        return response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}

interface TokenProvider {
    fun getAccessToken(): String?
}


val tokenProvider = object : TokenProvider {
    override fun getAccessToken(): String? {
        val request = Request.Builder().url(authorizationUrl).build()
        val client = OkHttpClient.Builder().build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to get access token:" + response.code + " " + response.message)
            }
            val tokenResponse = response.body?.string()


            // Code pour obtenir l'accessToken depuis votre système de gestion d'authentification
            return tokenResponse
        }
    }
}

    val clientId = "votre_client_id"
    val redirectUri = "votre_uri_de_redirection"
    val scope = "les_scopes_demandes"

    val authorizationUrl = "https://api.intra.42.fr/oauth/authorize?" +
            "client_id=$clientId&" +
            "redirect_uri=$redirectUri&" +
            "scope=$scope&" +
            "response_type=code"

