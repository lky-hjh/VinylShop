package com.example.shoppingapp.ui.profile

import android.content.Context
import android.util.Log
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

    companion object {
        private const val TAG = "ProfileVM"
    }

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
        Log.d(TAG, "login() called — username='$username'")
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = userRepository.login(username, password)
            Log.d(TAG, "login: result=$result")
            _loginState.value = result

            if (result is Resource.Success) {
                Log.d(TAG, "login: 成功 → userId=${result.data.id}, 写入 prefs")
                prefs.edit()
                    .putString("user_id", result.data.id)
                    .putString("username", result.data.username)
                    .apply()
                Log.d(TAG, "login: prefs 写入完成")
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
        Log.d(TAG, "loadUser() called — userId from prefs='$userId'")
        if (userId.isEmpty()) {
            Log.w(TAG, "loadUser: userId 为空 → 未登录")
            _userState.value = Resource.Error("未登录")
            return
        }
        viewModelScope.launch {
            Log.d(TAG, "loadUser: 查询 DB userId=$userId")
            _userState.value = Resource.Loading
            val result = userRepository.getUserById(userId)
            Log.d(TAG, "loadUser: result=$result")
            _userState.value = when {
                result is Resource.Success -> {
                    Log.d(TAG, "loadUser: 用户查询成功 → username=${result.data.username}")
                    Resource.success(result.data)
                }
                result is Resource.Error && result.message == "用户不存在" -> {
                    Log.w(TAG, "loadUser: 用户不存在 → 清除 prefs，提示重新登录")
                    prefs.edit().clear().apply()
                    Log.d(TAG, "loadUser: prefs 已清除, currentUserId now='${currentUserId}'")
                    Resource.error("未登录，请重新登录")
                }
                result is Resource.Error -> {
                    Log.e(TAG, "loadUser: 其他错误 → ${result.message}")
                    Resource.error(result.message)
                }
                else -> Resource.Loading
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

    /**
     * 模拟第三方登录（微信/QQ）
     * provider: "wechat" or "qq"
     */
    fun loginWithThirdParty(provider: String) {
        val username = when (provider) {
            "wechat" -> "wechat_mock"
            "qq" -> "qq_mock"
            else -> {
                _loginState.value = Resource.error("不支持的登录方式")
                return
            }
        }
        Log.d(TAG, "loginWithThirdParty: provider=$provider, username=$username")
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            // 查找 mock 用户（seed 数据里已创建）
            val result = userRepository.login(username, "mock123")
            Log.d(TAG, "loginWithThirdParty: result=$result")
            _loginState.value = result

            if (result is Resource.Success) {
                Log.d(TAG, "loginWithThirdParty: 成功 → userId=${result.data.id}, 写入 prefs")
                prefs.edit()
                    .putString("user_id", result.data.id)
                    .putString("username", result.data.username)
                    .apply()
                Log.d(TAG, "loginWithThirdParty: prefs 写入完成")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}
