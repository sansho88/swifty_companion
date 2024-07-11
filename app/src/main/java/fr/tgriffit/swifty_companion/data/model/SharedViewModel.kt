package fr.tgriffit.swifty_companion.data.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService

class SharedViewModel : ViewModel() {

    private val gson = Gson()
    private val _searchQuery = MutableLiveData<String>()
    private val _result = MutableLiveData<String?>()
    private val _user = MutableLiveData<User>()
    private val _apiService = MutableLiveData<ApiService>()
    val searchQuery: LiveData<String> = _searchQuery
    val user: LiveData<User?> = _user
    val result: LiveData<String?> = _result
    val apiService: LiveData<ApiService> = _apiService

    fun setSearchQuery(query: String): SharedViewModel {
        _searchQuery.value = query
        return this
    }
    fun setResult(result: String?): SharedViewModel {
        _result.value = result
        return this
    }
    fun setUser(user: User): SharedViewModel {
        _user.value = user
        return this
    }
    fun setApiService(apiService: ApiService): SharedViewModel {
        _apiService.value = apiService
        return this
    }

    /**
     * Get the user from the result.
     * @return the user or null if the result variable is null or empty
     * @see performSearch
     */
    fun getUserFromResult(): User? {
        if (!result.isInitialized || result.value.isNullOrEmpty()) {
            Log.e("SharedViewModel", "getUserFromResult: result is null or empty")
            return null
        }
        try {
            setUser(gson.fromJson(result.value, User::class.java))
            return user.value
        } catch (e: JsonSyntaxException) {
            Log.e(
                "SharedViewModel", "getUserFromResult: " +
                        "The result can't be converted into User object"
            )
            return null
        }
    }

    /**
     * Perform a search on the API.
     * The result is stored in the result variable.
     */
    fun performSearch(): SharedViewModel {
        if (!apiService.isInitialized) {
            Log.e("SharedViewModel", "performSearch: apiService is null")
            return this
        }
        setResult(apiService.value!!.getAbout(searchQuery.value))
        return this
    }
}
