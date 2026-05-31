package com.drs.auralife.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.repository.AvatarRepository
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

    private val _avatarResult = MutableSharedFlow<Result<Unit>>()
    val avatarResult: SharedFlow<Result<Unit>> = _avatarResult.asSharedFlow()

    fun loadPremiumStatus() {
        viewModelScope.launch {
            try {
                _premiumStatus.value = getPremiumStatusUseCase()
            } catch (e: Exception) {
                Log.e("MainViewModel", "loadPremiumStatus failed", e)
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            try {
                _avatarState.value = avatarRepository.getAvatar()
            } catch (e: Exception) {
                Log.e("MainViewModel", "loadAvatar failed", e)
                _avatarState.value = null
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
                    _avatarResult.emit(Result.success(Unit))
                    loadAvatar()
                } else {
                    _avatarResult.emit(Result.failure(Exception("Upload failed")))
                }
            } catch (e: Exception) {
                _avatarResult.emit(Result.failure(e))
            }
        }
    }
}
