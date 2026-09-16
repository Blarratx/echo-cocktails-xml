package com.example.myapy

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapy.data.CocktailRepository
import com.example.myapy.databinding.ActivityDetailBinding
import com.example.myapy.ui.theme.ThemeManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            }
        }
    }

    private fun loadDetails(id: String) = lifecycleScope.launch {
        repository.getCocktailDetails(id).onSuccess { cocktail ->
            cocktail?.let {
                binding.detailName.text = it.name
                binding.detailCategory.text = "${it.category} | ${it.glass}"
                binding.detailInstructions.text = it.instructions
                binding.detailIngredients.text = it.getIngredientsWithMeasures().joinToString("\n")
                
                Glide.with(this@DetailActivity)
                    .load(it.imageUrl)
                    .into(binding.detailImage)
            }
        }
    }
}
