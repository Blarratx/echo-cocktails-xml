package com.example.myapy.model

import com.google.gson.annotations.SerializedName

data class CocktailResponse(
    @SerializedName("drinks") val drinks: List<Cocktail>?
)

data class Cocktail(
    @SerializedName("idDrink") val id: String,
    @SerializedName("strDrink") val name: String,
    @SerializedName("strCategory") val category: String?,
    @SerializedName("strInstructions") val instructions: String?,
    @SerializedName("strDrinkThumb") val imageUrl: String,
    @SerializedName("strGlass") val glass: String?,
    
    // Ingredientes (API returns up to 15, we take 6 for brevity or more if needed)
    val strIngredient1: String?, val strIngredient2: String?, val strIngredient3: String?,
    val strIngredient4: String?, val strIngredient5: String?, val strIngredient6: String?,
    // Medidas
    val strMeasure1: String?, val strMeasure2: String?, val strMeasure3: String?,
    val strMeasure4: String?, val strMeasure5: String?, val strMeasure6: String?
) {
    /**
     * Devuelve una lista de ingredientes con sus medidas, filtrando nulos o vacíos.
     */
    fun getIngredientsWithMeasures(): List<String> {
        val ingredients = listOf(strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5, strIngredient6)
        val measures = listOf(strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5, strMeasure6)
        
        return ingredients.zip(measures)
            .filter { it.first != null && it.first!!.isNotBlank() }
            .map { "${it.second ?: ""} ${it.first}".trim() }
    }
}

data class CategoryResponse(
    @SerializedName("drinks") val categories: List<Category>
)

data class Category(
    @SerializedName("strCategory") val name: String
)
