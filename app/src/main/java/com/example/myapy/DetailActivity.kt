package com.example.myapy

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapy.data.CocktailRepository
import com.example.myapy.databinding.ActivityDetailBinding
import com.example.myapy.ui.theme.ThemeManager
import com.example.myapy.utils.DataTranslator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repository = CocktailRepository()
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = ThemeManager(this)
        observeTheme()

        binding.backButton.setOnClickListener { finish() }

        // Modo Bartender: toggle que mantiene la pantalla encendida
        binding.bartenderToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                binding.bartenderToggle.text = getString(R.string.bartender_on)
                Toast.makeText(this, getString(R.string.bartender_on), Toast.LENGTH_SHORT).show()
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                binding.bartenderToggle.text = getString(R.string.bartender_off)
                Toast.makeText(this, getString(R.string.bartender_off), Toast.LENGTH_SHORT).show()
            }
        }

        val cocktailId = intent.getStringExtra("COCKTAIL_ID") ?: return
        loadDetails(cocktailId)
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            themeManager.themeFlow.collectLatest { theme ->
                binding.detailRoot.setBackgroundColor(theme.backgroundColor)
                binding.detailName.setTextColor(theme.accentColor)
                binding.ingredientsLabel.setTextColor(theme.accentColor)
                binding.instructionsLabel.setTextColor(theme.accentColor)
                binding.detailCategory.setTextColor(theme.textColor)
                binding.detailIngredients.setTextColor(theme.textColor)
                binding.detailInstructions.setTextColor(theme.textColor)
                binding.backButton.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
                binding.backButton.setTextColor(theme.cardColor)

                // Aplicar el color del tema al toggle Bartender
                binding.bartenderToggle.setTextColor(theme.accentColor)
                binding.bartenderToggle.thumbTintList = ColorStateList.valueOf(theme.accentColor)
                binding.bartenderToggle.trackTintList = ColorStateList.valueOf(theme.secondaryColor)
            }
        }
    }

    private fun loadDetails(id: String) = lifecycleScope.launch {
        repository.getCocktailDetails(id).onSuccess { cocktail ->
            cocktail?.let {
                binding.detailName.text = it.name
                
                // Traducción de Categoría y Copa
                val translatedCat = DataTranslator.translate(
                    it.category, DataTranslator.TranslationType.CATEGORY
                )
                val translatedGlass = DataTranslator.translate(
                    it.glass, DataTranslator.TranslationType.GLASS
                )
                binding.detailCategory.text = "$translatedCat | $translatedGlass"

                // Seleccionar instrucciones según el idioma del sistema
                val lang = Locale.getDefault().language
                val localizedInstructions = if (lang == "es") {
                    it.instructionsES ?: it.instructions
                } else {
                    it.instructions
                }
                
                binding.detailInstructions.text = localizedInstructions
                
                // Traducción de cada Ingrediente
                val translatedIngredients = it.getIngredientsWithMeasures().map { ing ->
                    DataTranslator.translate(ing, DataTranslator.TranslationType.INGREDIENT)
                }
                binding.detailIngredients.text = translatedIngredients.joinToString("\n")

                Glide.with(this@DetailActivity)
                    .load(it.imageUrl)
                    .into(binding.detailImage)
            }
        }
    }
}