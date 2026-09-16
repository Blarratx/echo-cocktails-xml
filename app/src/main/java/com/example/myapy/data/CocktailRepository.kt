package com.example.myapy.data

import com.example.myapy.data.network.RetrofitClient
import com.example.myapy.model.Category
import com.example.myapy.model.Cocktail

class CocktailRepository {
    private val api = RetrofitClient.apiService

    suspend fun searchCocktails(query: String): Result<List<Cocktail>> = try {
        val response = api.searchCocktails(query)
        Result.success(response.drinks ?: emptyList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCategories(): Result<List<Category>> = try {
        val response = api.getCategories()
        Result.success(response.categories)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun filterByCategory(category: String): Result<List<Cocktail>> = try {
        val response = api.filterByCategory(category)
        Result.success(response.drinks ?: emptyList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCocktailDetails(id: String): Result<Cocktail?> = try {
        val response = api.getCocktailDetails(id)
        Result.success(response.drinks?.firstOrNull())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
