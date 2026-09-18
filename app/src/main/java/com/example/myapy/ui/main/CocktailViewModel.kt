package com.example.myapy.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapy.data.CocktailRepository
import com.example.myapy.model.Category
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlin.time.Duration.Companion.milliseconds

class CocktailViewModel : ViewModel() {
    private val repository = CocktailRepository()

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    private var searchJob: Job? = null
    private var allCategories: List<Category> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() = viewModelScope.launch {
        _uiState.value = MainUiState.Loading
        val catResult = repository.getCategories()
        
        catResult.onSuccess { categories ->
            allCategories = categories
            searchCocktails("m") // Carga inicial con 'm' para simular populares
        }.onFailure {
            _uiState.value = MainUiState.Error("Error al cargar categorías ECHOnet")
        }
    }

    fun searchCocktails(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) delay(500.milliseconds) // Debounce
            
            _uiState.value = MainUiState.Loading
            repository.searchCocktails(query).onSuccess { drinks ->
                _uiState.value = MainUiState.Success(drinks, allCategories)
            }.onFailure {
                _uiState.value = MainUiState.Error("Error de conexión con ECHOnet")
            }
        }
    }
}
