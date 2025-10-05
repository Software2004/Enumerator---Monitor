package com.example.enumerator_monitor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enumerator_monitor.data.User
import com.example.enumerator_monitor.data.UserRole
import com.example.enumerator_monitor.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    init {
        _currentUser.value = authRepository.getCurrentUser()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                result.fold(
                    onSuccess = { user ->
                        _currentUser.value = user
                        _successMessage.value = "Login successful"
                        loadUserData(user.uid)
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Login failed"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        blockNo: String
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank() || blockNo.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.signup(email, password, name, role, blockNo)
                result.fold(
                    onSuccess = { user ->
                        _currentUser.value = user
                        _successMessage.value = "Account created successfully"
                        loadUserData(user.uid)
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Signup failed"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Signup failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Please enter your email address"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.resetPassword(email)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Password reset link sent to your email"
                    },
                    onFailure = { exception ->
                        _errorMessage.value = exception.message ?: "Failed to send reset link"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to send reset link"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.getUserData(uid)
                result.fold(
                    onSuccess = { user ->
                        _userData.value = user
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Failed to load user data: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user data: ${e.message}"
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        _userData.value = null
        _successMessage.value = "Logged out successfully"
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}