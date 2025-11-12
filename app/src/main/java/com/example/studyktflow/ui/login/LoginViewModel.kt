package com.example.studyktflow.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyktflow.data.model.User
import com.example.studyktflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    
    private val repository = AuthRepository()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<LoginState>(LoginState.Idle)
    val registerState: StateFlow<LoginState> = _registerState.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(username, password).collect { result ->
                result.onSuccess { user ->
                    _loginState.value = LoginState.Success(user)
                }.onFailure { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "登录失败")
                }
            }
        }
    }
    
    fun register(username: String, password: String, repassword: String) {
        viewModelScope.launch {
            _registerState.value = LoginState.Loading
            repository.register(username, password, repassword).collect { result ->
                result.onSuccess { user ->
                    _registerState.value = LoginState.Success(user)
                }.onFailure { exception ->
                    _registerState.value = LoginState.Error(exception.message ?: "注册失败")
                }
            }
        }
    }
    
    fun resetState() {
        _loginState.value = LoginState.Idle
        _registerState.value = LoginState.Idle
    }
}
