package com.example.myapy

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.myapy.databinding.ActivityDetailBinding
import com.example.myapy.model.Cocktail
import com.example.myapy.ui.detail.DetailUiState
import com.example.myapy.ui.detail.DetailViewModel
import com.example.myapy.ui.theme.AppTheme
import com.example.myapy.ui.theme.ThemeManager
import com.example.myapy.utils.DataTranslator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = ThemeManager(this)

        setupListeners()
        observeTheme()
        observeViewModel()

        val cocktailId = intent.getStringExtra("COCKTAIL_ID")
        if (cocktailId != null) {
            viewModel.loadCocktailDetails(cocktailId)
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupListeners() {
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
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeManager.themeFlow.collectLatest { theme ->
                    applyTheme(theme)
                }
            }
        }
    }

    private fun applyTheme(theme: AppTheme) {
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is DetailUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.detailScrollView.visibility = View.GONE
                        }
                        is DetailUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.detailScrollView.visibility = View.VISIBLE
                            bindCocktailData(state.cocktail)
                        }
                        is DetailUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@DetailActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindCocktailData(cocktail: Cocktail) {
        binding.detailName.text = cocktail.name

        // Traducción de Categoría y Copa
        val translatedCat = DataTranslator.translate(
            cocktail.category, DataTranslator.TranslationType.CATEGORY
        )
        val translatedGlass = DataTranslator.translate(
            cocktail.glass, DataTranslator.TranslationType.GLASS
        )
        
        binding.detailCategory.text = listOf(translatedCat, translatedGlass)
            .filter { it.isNotBlank() }
            .joinToString(" | ")

        // Seleccionar instrucciones según el idioma del sistema
        val lang = Locale.getDefault().language
        val localizedInstructions = if (lang == "es") {
            cocktail.instructionsES.takeIf { !it.isNullOrBlank() } ?: cocktail.instructions
        } else {
            cocktail.instructions
        }

        binding.detailInstructions.text = localizedInstructions.orEmpty()

        // Traducción de cada Ingrediente
        val translatedIngredients = cocktail.getIngredientsWithMeasures().map { ing ->
            DataTranslator.translate(ing, DataTranslator.TranslationType.INGREDIENT)
        }
        binding.detailIngredients.text = translatedIngredients.joinToString("\n")

        Glide.with(this)
            .load(cocktail.imageUrl)
            .centerCrop()
            .into(binding.detailImage)
    }
}
