package com.example.myapy.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapy.data.CocktailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository = CocktailRepository()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentId: String? = null

    fun loadCocktailDetails(id: String) {
        if (currentId == id && _uiState.value is DetailUiState.Success) {
            return
        }
        currentId = id
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getCocktailDetails(id)
                .onSuccess { cocktail ->
                    if (cocktail != null) {
                        _uiState.value = DetailUiState.Success(cocktail)
                    } else {
                        _uiState.value = DetailUiState.Error("No se encontraron detalles del cóctel")
                    }
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error("Error de conexión con ECHOnet")
                }
        }
    }
}
