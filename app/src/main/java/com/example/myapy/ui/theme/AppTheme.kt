package com.example.myapy.ui.theme

import android.graphics.Color

enum class ThemeType { PANDORA, HYPERION, MALIWAN, JAKOBS, GUAC, TORGUE }

data class AppTheme(
    val type: ThemeType,
    val backgroundColor: Int,
    val accentColor: Int,
    val secondaryColor: Int,
    val textColor: Int = Color.BLACK,
    val cardColor: Int = Color.WHITE
) {
    companion object {
        fun getTheme(type: ThemeType): AppTheme = when (type) {
            // PANDORA: El desierto, óxido y Crimson Raiders
            ThemeType.PANDORA -> AppTheme(
                type,
                Color.parseColor("#1C1B17"), // Fondo marrón-negro
                Color.parseColor("#FF6B00"), // Naranja Pandora
                Color.parseColor("#C1121F"), // Rojo Crimson
                Color.parseColor("#F2E8CF"), // Texto crema
                Color.parseColor("#2D2C26")  // Card gris-marrón
            )
            // HYPERION: Corporativo, limpio, alta tecnología
            ThemeType.HYPERION -> AppTheme(
                type,
                Color.parseColor("#F5F1E8"), // Blanco hueso
                Color.parseColor("#FFCC00"), // Amarillo Hyperion
                Color.parseColor("#0077B6"), // Azul corporativo
                Color.parseColor("#1A1A1A"), // Texto negro
                Color.WHITE                  // Card blanco puro
            )
            // MALIWAN: Elemental, neón, tecnológico
            ThemeType.MALIWAN -> AppTheme(
                type,
                Color.parseColor("#0A1F1A"), // Verde oscuro tóxico
                Color.parseColor("#00F5D4"), // Cian eléctrico
                Color.parseColor("#FF006E"), // Rosa neón
                Color.parseColor("#E0FFF4"), // Texto blanco-cian
                Color.parseColor("#14352D")  // Card verde profundo
            )
            // JAKOBS: Madera, cuero, tradición "One Shot, One Kill"
            ThemeType.JAKOBS -> AppTheme(
                type,
                Color.parseColor("#2A1F14"), // Marrón tabaco
                Color.parseColor("#D4A373"), // Latón/Ámbar
                Color.parseColor("#8B0000"), // Rojo madera
                Color.parseColor("#F5E6D3"), // Texto pergamino
                Color.parseColor("#3D2B1F")  // Card madera oscura
            )
            // GUAC SHOCK: Estilo visual de Borderlands 3
            ThemeType.GUAC -> AppTheme(
                type,
                Color.parseColor("#1F2A0F"), // Verde oliva
                Color.parseColor("#C6FF00"), // Lima eléctrico
                Color.parseColor("#FF3D00"), // Naranja fuego
                Color.parseColor("#F0FFE0"), // Texto claro
                Color.parseColor("#2D3B1A")  // Card verde militar
            )
            // TORGUE: ¡EXPLOSIONES!, Negro, Amarillo y cuadros de carrera
            ThemeType.TORGUE -> AppTheme(
                type,
                Color.parseColor("#000000"), // Negro total
                Color.parseColor("#FFD60A"), // Amarillo explosivo
                Color.parseColor("#D90429"), // Rojo peligro
                Color.parseColor("#FFFFFF"), // Texto blanco
                Color.parseColor("#1F1F1F")  // Card gris oscuro
            )
        }
    }
}
