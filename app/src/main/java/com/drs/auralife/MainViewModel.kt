package com.drs.auralife

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.repository.AvatarRepository
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.usecase.GetPremiumStatusUseCase
import com.drs.auralife.domain.usecase.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

sealed interface MainUiEffect {
    data class ShowToast(val message: String) : MainUiEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val authRepository: AuthRepository,
    private val avatarRepository: AvatarRepository,
) : ViewModel() {

    val authState: StateFlow<Boolean> = authRepository.authState

    val userEmail: String? get() = authRepository.getEmail()

    private val _premiumStatus = MutableStateFlow<PremiumStatus?>(null)
    val premiumStatus: StateFlow<PremiumStatus?> = _premiumStatus.asStateFlow()

    private val _avatarState = MutableStateFlow<Bitmap?>(null)
    val avatarState: StateFlow<Bitmap?> = _avatarState.asStateFlow()

    private val _effect = MutableSharedFlow<MainUiEffect>()
    val effect: SharedFlow<MainUiEffect> = _effect.asSharedFlow()

    fun loadPremiumStatus() {
        viewModelScope.launch {
            when (val result = getPremiumStatusUseCase()) {
                is Result.Success -> _premiumStatus.value = result.data
                is Result.Error -> Log.e("MainViewModel", "loadPremiumStatus failed", result.exception)
                is Result.Loading -> {}
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            when (val result = avatarRepository.getAvatar()) {
                is Result.Success -> {
                    val bytes = result.data
                    _avatarState.value = if (bytes != null) {
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    } else null
                }

                is Result.Error -> {
                    Log.e("MainViewModel", "loadAvatar failed", result.exception)
                    _avatarState.value = null
                }

                is Result.Loading -> {}
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun uploadAvatar(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val imageBytes = stream.toByteArray()
                val success = uploadAvatarUseCase(imageBytes)
                if (success) {
                    loadAvatar()
                    _effect.emit(MainUiEffect.ShowToast("Upload avatar successfully"))
                } else {
                    _effect.emit(MainUiEffect.ShowToast("Upload avatar failed"))
                }
            } catch (e: Exception) {
                _effect.emit(MainUiEffect.ShowToast(e.message ?: "Upload avatar failed"))
            }
        }
    }
}
