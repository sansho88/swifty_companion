package fr.tgriffit.swifty_companion.data.auth

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.versionedparcelable.VersionedParcelize
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Names SHOULD follow snake_case typo for Gson parsing
 */
@VersionedParcelize
data class Token(
    var access_token: String = "null",
    var token_type: String = "Bearer",
    var expires_in: Number,
    var scope: String = "Public",
    var created_at : Int = 0,
    var refresh_token: String = "null"
    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString()
    ) {}

    override fun toString(): String {
        return "Token(access_token='$access_token', \n" +
                "token_type='$token_type', \n" +
                "expires_in=$expires_in, \n" +
                "scope='$scope', \n" +
                "created_at=$created_at" +
                "refresh_token='$refresh_token')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(access_token)
        parcel.writeString(token_type)
        parcel.writeLong(expires_in.toLong())
        parcel.writeString(scope)
        parcel.writeInt(created_at)
        parcel.writeString(refresh_token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Token> {
        override fun createFromParcel(parcel: Parcel): Token {
            return Token(parcel)
        }

        override fun newArray(size: Int): Array<Token?> {
            return arrayOfNulls(size)
        }

        /**
         * Exchange the code for a token
         * A 401 error is "thrown" when the app's token in the 42 API is expired.
         */
        fun createTokenFromCode(code: String?): Token?{
            var token : Token ? = null
            if (!code.isNullOrEmpty()) {
                val executor = Executors.newSingleThreadExecutor() //for API calls!
                executor.execute {
                    try {
                        token = ApiService().exchangeCodeForToken42(code)
                    }catch (exception: Exception){
                        Log.e("Token", exception.toString())
                    }
                    executor.shutdown()
                }
                if (executor.awaitTermination(42, TimeUnit.SECONDS))
                    return token

            }
            return null
        }
    }
}
