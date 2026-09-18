package com.example.myapy.utils

import java.util.Locale

object DataTranslator {

    private val categories = mapOf(
        "Ordinary Drink" to "Bebida Común",
        "Cocktail" to "Cóctel",
        "Shake" to "Batido",
        "Other / Unknown" to "Otro / Desconocido",
        "Cocoa" to "Cacao",
        "Shot" to "Chupito",
        "Coffee / Tea" to "Café / Té",
        "Homemade Liqueur" to "Licor Casero",
        "Punch / Party Drink" to "Ponche / Bebida de Fiesta",
        "Beer" to "Cerveza",
        "Soft Drink" to "Refresco"
    )

    private val glasses = mapOf(
        "Highball glass" to "Vaso Highball",
        "Cocktail glass" to "Copa de Cóctel",
        "Old-fashioned glass" to "Vaso Old-fashioned",
        "Whiskey sour glass" to "Vaso Whiskey Sour",
        "Champagne flute" to "Copa de Champán",
        "Collins glass" to "Vaso Collins",
        "Pousse cafe glass" to "Vaso Pousse Café",
        "Hot drink mug" to "Taza para Bebida Caliente",
        "Beer mug" to "Jarra de Cerveza",
        "Hurricane glass" to "Copa Huracán",
        "Martini Glass" to "Copa de Martini",
        "Shot glass" to "Vaso de Chupito"
    )

    private val ingredients = mapOf(
        "Light rum" to "Ron blanco", "Dark rum" to "Ron añejo", "Vodka" to "Vodka",
        "Gin" to "Ginebra", "Tequila" to "Tequila", "Triple sec" to "Triple seco",
        "Lime juice" to "Zumo de lima", "Lemon juice" to "Zumo de limón",
        "Sugar" to "Azúcar", "Water" to "Agua", "Ice" to "Hielo",
        "Milk" to "Leche", "Coffee" to "Café", "Orange juice" to "Zumo de naranja",
        "Salt" to "Sal", "Grenadine" to "Granadina", "Egg white" to "Clara de huevo"
    )

    fun translate(text: String?, type: TranslationType): String {
        val nonNullText = text ?: return ""
        if (Locale.getDefault().language != "es") return nonNullText

        return when (type) {
            TranslationType.CATEGORY -> categories[nonNullText] ?: nonNullText
            TranslationType.GLASS -> glasses[nonNullText] ?: nonNullText
            TranslationType.INGREDIENT -> {
                var translated = nonNullText
                ingredients.forEach { (en, es) ->
                    translated = translated.replace(en, es, ignoreCase = true)
                }
                translated
            }
        }
    }

    enum class TranslationType { CATEGORY, GLASS, INGREDIENT }
}
