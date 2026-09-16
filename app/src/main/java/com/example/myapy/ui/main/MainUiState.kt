package com.example.myapy.ui.main

import com.example.myapy.model.Cocktail
import com.example.myapy.model.Category

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val cocktails: List<Cocktail>, val categories: List<Category>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}
