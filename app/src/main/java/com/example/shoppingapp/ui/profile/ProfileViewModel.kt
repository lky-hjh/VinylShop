package com.example.shoppingapp.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.UserRepository
import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)

    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<User>?>(null)
    val registerState: StateFlow<Resource<User>?> = _registerState.asStateFlow()

    private val _userState = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val userState: StateFlow<Resource<User>?> = _userState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<User>?>(null)
    val updateState: StateFlow<Resource<User>?> = _updateState.asStateFlow()

    val isLoggedIn: Boolean
        get() = prefs.getString("user_id", null) != null

    val currentUserId: String
        get() = prefs.getString("user_id", "") ?: ""

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = userRepository.login(username, password)
            _loginState.value = result

            if (result is Resource.Success) {
                prefs.edit()
                    .putString("user_id", result.data.id)
                    .putString("username", result.data.username)
                    .apply()
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val result = userRepository.register(username, email, password)
            _registerState.value = result
        }
    }

    fun loadUser() {
        val userId = currentUserId
        if (userId.isEmpty()) {
            _userState.value = Resource.Error("未登录")
            return
        }
        viewModelScope.launch {
            userRepository.getUserByIdFlow(userId).collect { resource ->
                _userState.value = when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            Resource.success(resource.data)
                        } else {
                            Resource.error("用户不存在")
                        }
                    }
                    is Resource.Error -> Resource.error(resource.message)
                    is Resource.Loading -> Resource.Loading
                }
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading
            val result = userRepository.updateUser(user)
            _updateState.value = result
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _userState.value = Resource.Error("已退出")
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}
