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
    @SerializedName("strInstructionsES") val instructionsES: String?,
    @SerializedName("strDrinkThumb") val imageUrl: String?,
    @SerializedName("strGlass") val glass: String?,

    // Ingredientes (API returns up to 15)
    val strIngredient1: String? = null, val strIngredient2: String? = null,
    val strIngredient3: String? = null, val strIngredient4: String? = null,
    val strIngredient5: String? = null, val strIngredient6: String? = null,
    val strIngredient7: String? = null, val strIngredient8: String? = null,
    val strIngredient9: String? = null, val strIngredient10: String? = null,
    val strIngredient11: String? = null, val strIngredient12: String? = null,
    val strIngredient13: String? = null, val strIngredient14: String? = null,
    val strIngredient15: String? = null,

    // Medidas
    val strMeasure1: String? = null, val strMeasure2: String? = null,
    val strMeasure3: String? = null, val strMeasure4: String? = null,
    val strMeasure5: String? = null, val strMeasure6: String? = null,
    val strMeasure7: String? = null, val strMeasure8: String? = null,
    val strMeasure9: String? = null, val strMeasure10: String? = null,
    val strMeasure11: String? = null, val strMeasure12: String? = null,
    val strMeasure13: String? = null, val strMeasure14: String? = null,
    val strMeasure15: String? = null
) {
    /**
     * Devuelve una lista de ingredientes con sus medidas, filtrando nulos o vacíos.
     */
    fun getIngredientsWithMeasures(): List<String> {
        val ingredients = listOf(
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
            strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15
        )
        val measures = listOf(
            strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
            strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
            strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15
        )

        return ingredients.asSequence()
            .zip(measures.asSequence())
            .filter { (ing, _) -> !ing.isNullOrBlank() }
            .map { (ing, measure) ->
                val m = measure?.trim().orEmpty()
                if (m.isNotEmpty()) "$m ${ing!!.trim()}" else ing!!.trim()
            }
            .toList()
    }
}

data class CategoryResponse(
    @SerializedName("drinks") val categories: List<Category>
)

data class Category(
    @SerializedName("strCategory") val name: String
)
