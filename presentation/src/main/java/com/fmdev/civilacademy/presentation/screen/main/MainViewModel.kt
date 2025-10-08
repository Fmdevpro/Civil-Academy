package com.fmdev.civilacademy.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<MainSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<MainSideEffect> = _sideEffect.receiveAsFlow()

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.HideSystemUI -> handleHideSystemUI()
            is MainUiEvent.ShowSystemUI -> handleShowSystemUI()
        }
    }

    private fun handleHideSystemUI() {
        _uiState.update { it.copy(isSystemUIVisible = false) }
        viewModelScope.launch {
            _sideEffect.send(MainSideEffect.HideSystemUI)
        }
    }

    private fun handleShowSystemUI() {
        _uiState.update { it.copy(isSystemUIVisible = true) }
        viewModelScope.launch {
            _sideEffect.send(MainSideEffect.ShowSystemUI)
        }
    }
}