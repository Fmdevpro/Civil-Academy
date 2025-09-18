package com.fmdev.civilacademy.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    private val _isSystemUIVisible = MutableStateFlow(false)
    val isSystemUIVisible: StateFlow<Boolean> get() = _isSystemUIVisible

    fun hideSystemUI() {
        _isSystemUIVisible.value = false
    }

    fun showSystemUI() {
        _isSystemUIVisible.value = true
    }
}