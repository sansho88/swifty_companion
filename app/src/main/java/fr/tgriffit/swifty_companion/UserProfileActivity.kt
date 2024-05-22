package fr.tgriffit.swifty_companion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import fr.tgriffit.swifty_companion.data.auth.ApiService
import fr.tgriffit.swifty_companion.data.model.WeatherOfDay
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val TAG = "UserProfileActivity"


class UserProfileActivity : AppCompatActivity() {


    /*lateinit var apiResponse : String*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myBuilder = CronetEngine.Builder(this.baseContext)
        val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()

        setContentView(R.layout.user_profile)


    }
}

class MyUrlRequestCallback : UrlRequest.Callback() {

    lateinit var response: String


    override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
        Log.i(TAG, "onRedirectReceived method called.")
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(TAG, "onResponseStarted method called.")
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes for the read() method. The same buffer
        // with data is passed to the onReadCompleted() method.
        request?.read(ByteBuffer.allocateDirect(102400))
    }

    override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
        Log.i(TAG, "onReadCompleted method called.")
        // You should keep reading the request until there's no more data.
        byteBuffer?.clear()
        request?.read(byteBuffer)
        Log.i(TAG, "onReadCompleted: Request data: $byteBuffer}")
        
        var tmpBuffer: String = ""
        if (byteBuffer != null) {
            for (byte in byteBuffer.array())
               if (byte > 0 )
                    tmpBuffer += byte.toInt().toChar()
        }
        //tmpBuffer += 0
        response = tmpBuffer.substring(0, tmpBuffer.length - 4)
        //File("apiLogsResponse").writeText(response)

        Log.i(TAG, "onReadCompleted: response =$response")
    }
    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        Log.i(TAG, "onSucceeded method called.")
        Log.i(TAG, "onSucceeded: UrlResponseInfo= $info")
        val gson = Gson()
        val weather = gson.fromJson(response, WeatherOfDay::class.java)
        Log.i(TAG, "onSucceeded: ${weather}")
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        TODO("Not yet implemented")
    }
}
