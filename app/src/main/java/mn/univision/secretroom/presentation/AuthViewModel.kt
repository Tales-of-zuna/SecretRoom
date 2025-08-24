package mn.univision.secretroom.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.repositories.AuthRepository
import mn.univision.secretroom.data.repositories.ViewsRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository, private val viewsRepository: ViewsRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            if (authRepository.isAuthenticated()) {
                _authState.value = AuthState.Success
            }
        }
    }

    fun authenticate() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = authRepository.performAuthentication()) {
                is AuthRepository.AuthResult.Success -> {
                    _authState.value = AuthState.Success
                    viewsRepository.fetchViews()
                }

                is AuthRepository.AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }

                is AuthRepository.AuthResult.Loading -> {
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Initial
        }
    }

    fun retryAuthentication() {
        authenticate()
    }
}