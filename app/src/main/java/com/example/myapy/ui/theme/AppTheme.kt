package com.example.myapy.ui.theme

import android.graphics.Color

enum class ThemeType { PANDORA, HYPERION, MALIWAN, JAKOBS }

data class AppTheme(
    val type: ThemeType,
    val backgroundColor: Int,
    val accentColor: Int,
    val secondaryColor: Int,
    val textColor: Int = Color.BLACK
) {
    companion object {
        fun getTheme(type: ThemeType): AppTheme = when (type) {
            ThemeType.PANDORA -> AppTheme(
                type, 
                Color.parseColor("#1A1A1A"), 
                Color.parseColor("#FFC300"), 
                Color.parseColor("#E63946"), 
                Color.WHITE
            )
            ThemeType.HYPERION -> AppTheme(
                type, 
                Color.parseColor("#F1FAEE"), 
                Color.parseColor("#FFC300"), 
                Color.parseColor("#2B2B2B"), 
                Color.BLACK
            )
            ThemeType.MALIWAN -> AppTheme(
                type, 
                Color.parseColor("#0D1B2A"), 
                Color.parseColor("#06D6A0"), 
                Color.parseColor("#118AB2"), 
                Color.WHITE
            )
            ThemeType.JAKOBS -> AppTheme(
                type, 
                Color.parseColor("#2B2118"), 
                Color.parseColor("#D4A373"), 
                Color.parseColor("#8B5E34"), 
                Color.WHITE
            )
        }
    }
}
