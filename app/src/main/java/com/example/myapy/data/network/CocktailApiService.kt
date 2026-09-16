package com.example.myapy.data.network

import com.example.myapy.model.CategoryResponse
import com.example.myapy.model.CocktailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {
    @GET("search.php")
    suspend fun searchCocktails(@Query("s") name: String): CocktailResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): CocktailResponse

    @GET("lookup.php")
    suspend fun getCocktailDetails(@Query("i") id: String): CocktailResponse

    @GET("list.php?c=list")
    suspend fun getCategories(): CategoryResponse
}
