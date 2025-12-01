package com.example.practice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.domain.model.Profile
import com.example.practice.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow(Profile())
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.profileFlow.collect { saved ->
                _profile.value = saved
            }
        }
    }

    fun updateFullName(value: String) {
        _profile.update { it.copy(fullName = value) }
    }

    fun updateResumeUrl(value: String) {
        _profile.update { it.copy(resumeUrl = value) }
    }

    fun updatePosition(value: String) {
        _profile.update { it.copy(position = value) }
    }

    fun updateAvatar(uriString: String) {
        _profile.update { it.copy(avatarUri = uriString) }
    }

    fun updateFavoritePairTime(value: String) {
        _profile.update { it.copy(favoritePairTime = value) }
    }

    suspend fun save() {
        repository.saveProfile(_profile.value)
    }
}


