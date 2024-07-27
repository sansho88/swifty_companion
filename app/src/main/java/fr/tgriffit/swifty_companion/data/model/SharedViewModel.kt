package fr.tgriffit.swifty_companion.data.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.tgriffit.swifty_companion.data.User
import fr.tgriffit.swifty_companion.data.auth.ApiService
import kotlinx.coroutines.launch
import kotlin.math.log

class SharedViewModel : ViewModel() {

    private val gson = Gson()
    private val _searchQuery = MutableLiveData<String>()
    private val _result = MutableLiveData<String?>()
    private val _user = MutableLiveData<User>()
    private val _apiService = MutableLiveData<ApiService>()
    private val _index = MutableLiveData<Int>()
    private val _currentCursus = MutableLiveData<UserData.CursusUser>()
    private val _projectsList = MutableLiveData<List<UserData.ProjectsUsers>>()
    val searchQuery: LiveData<String> = _searchQuery
    val user: LiveData<User?> = _user
    val result: LiveData<String?> = _result
    val apiService: LiveData<ApiService> = _apiService
    val index: LiveData<Int> = _index
    val currentCursus: LiveData<UserData.CursusUser> = _currentCursus
    val projectsList: LiveData<List<UserData.ProjectsUsers>> = _projectsList
    
    fun setSearchQuery(query: String): SharedViewModel  {
        _searchQuery.postValue(query)
        _searchQuery.value = query
        return this
    }
    fun setResult(result: String?): SharedViewModel {
        _result.postValue(result)
        _result.value = result
        return this
    }
    fun setUser(user: User): SharedViewModel {
        _user.postValue(user)
        _user.value = user
        return this
    }
    fun setApiService(apiService: ApiService): SharedViewModel {
        _apiService.postValue(apiService)
        return this
    }

    fun setIndex(index: Int) {
        _index.postValue(index)
    }

    fun setCurrentCursus(cursus: UserData.CursusUser): SharedViewModel {
        _currentCursus.value = cursus
        return this
    }
    
    fun setProjectsList(projectsList: List<UserData.ProjectsUsers>): SharedViewModel {
        _projectsList.value = projectsList
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
        viewModelScope.launch {
            val apiResult = apiService.value?.getAbout(searchQuery.value)
            if (!apiResult.isNullOrEmpty())
                setResult(apiResult)
            else
                setResult(null)
        }
        return this
    }

    fun searchUser(login: String): User? {
        this.setSearchQuery(apiService.value!!.request.userByLogin(login))
        this.performSearch()
        val usersResult = result.value
        if (usersResult.isNullOrEmpty())
            return null
        val userResult = gson.fromJson(usersResult, Array<User>::class.java)[0]
        this.setSearchQuery(apiService.value!!.request.userById(userResult.id))
            .performSearch()

        return getUserFromResult()
    }
}
