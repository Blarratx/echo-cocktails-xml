package com.example.myapy.ui.detail

import com.example.myapy.model.Cocktail

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val cocktail: Cocktail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
