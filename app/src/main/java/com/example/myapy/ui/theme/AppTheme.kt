package com.example.myapy.ui.theme

import android.graphics.Color

enum class ThemeType { PANDORA, HYPERION, MALIWAN, JAKOBS, GUAC }

data class AppTheme(
    val type: ThemeType,
    val backgroundColor: Int,
    val accentColor: Int,
    val secondaryColor: Int,
    val textColor: Int = Color.BLACK
) {
    companion object {
        fun getTheme(type: ThemeType): AppTheme = when (type) {
            // Inspirado en "Crimson Blood Cell" / "Sand Line"
            ThemeType.PANDORA -> AppTheme(
                type,
                Color.parseColor("#1C1B17"), // fondo: marrón-negro desierto
                Color.parseColor("#FF6B00"), // acento: naranja Pandora
                Color.parseColor("#C1121F"), // secundario: rojo Crimson Raiders
                Color.parseColor("#F2E8CF")  // texto: crema
            )
            // Inspirado en "Executive Line" / "Titan Up the Graphics"
            ThemeType.HYPERION -> AppTheme(
                type,
                Color.parseColor("#F5F1E8"), // fondo: blanco hueso corporativo
                Color.parseColor("#FFCC00"), // acento: amarillo Hyperion
                Color.parseColor("#1A1A1A"), // secundario: negro corporativo
                Color.parseColor("#1A1A1A")  // texto: negro
            )
            // Inspirado en "Maliwalkie-Talkie" / "Electric Cell"
            ThemeType.MALIWAN -> AppTheme(
                type,
                Color.parseColor("#0A1F1A"), // fondo: verde-negro tóxico
                Color.parseColor("#00F5D4"), // acento: cian eléctrico Maliwan
                Color.parseColor("#FF006E"), // secundario: rosa neón
                Color.parseColor("#E0FFF4")  // texto: blanco verdoso
            )
            // Inspirado en "Antique Chic" / "Tropical Unknown Number"
            ThemeType.JAKOBS -> AppTheme(
                type,
                Color.parseColor("#2A1F14"), // fondo: marrón tabaco
                Color.parseColor("#D4A373"), // acento: latón envejecido
                Color.parseColor("#8B0000"), // secundario: rojo vino
                Color.parseColor("#F5E6D3")  // texto: pergamino
            )
            // Inspirado en "Guac Shock" — verde lima divertido
            ThemeType.GUAC -> AppTheme(
                type,
                Color.parseColor("#1F2A0F"), // fondo: verde oliva oscuro
                Color.parseColor("#C6FF00"), // acento: verde lima eléctrico
                Color.parseColor("#FF3D00"), // secundario: naranja fuego
                Color.parseColor("#F0FFE0")  // texto: blanco verdoso claro
            )
        }
    }
}