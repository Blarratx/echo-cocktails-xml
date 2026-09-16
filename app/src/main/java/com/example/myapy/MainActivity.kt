package com.example.myapy

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapy.databinding.ActivityMainBinding
import com.example.myapy.databinding.DialogUserProfileBinding
import com.example.myapy.ui.main.CocktailAdapter
import com.example.myapy.ui.main.CocktailViewModel
import com.example.myapy.ui.main.MainUiState
import com.example.myapy.ui.theme.AppTheme
import com.example.myapy.ui.theme.ThemeManager
import com.example.myapy.ui.theme.ThemeType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CocktailViewModel by viewModels()
    private lateinit var themeManager: ThemeManager
    private lateinit var cocktailAdapter: CocktailAdapter

    private val avatars = intArrayOf(
        com.example.myapy.R.drawable.ic_claptrap_standard,
        com.example.myapy.R.drawable.ic_claptrap_hyperion,
        com.example.myapy.R.drawable.ic_claptrap_maliwan,
        com.example.myapy.R.drawable.ic_claptrap_stealth
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = ThemeManager(this)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        observeTheme()
        observeUsername()
        observeAvatar()
    }

    private fun observeUsername() {
        lifecycleScope.launch {
            themeManager.usernameFlow.collectLatest { name ->
                binding.userNameTextView.text = (name ?: getString(R.string.default_username)).uppercase()
            }
        }
    }

    private fun observeAvatar() {
        lifecycleScope.launch {
            themeManager.avatarFlow.collectLatest { index ->
                binding.userAvatarIcon.setImageResource(avatars[index % avatars.size])
            }
        }
    }

    private fun setupRecyclerView() {
        cocktailAdapter = CocktailAdapter { cocktail ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("COCKTAIL_ID", cocktail.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cocktailAdapter
        }
    }

    private fun setupListeners() {
        binding.searchEditText.addTextChangedListener {
            viewModel.searchCocktails(it.toString())
        }

        // Al pulsar el indicador de señal, rotamos el tema
        binding.signalIndicator.setOnClickListener {
            rotateTheme()
        }

        binding.userProfileSection.setOnClickListener {
            showUserProfileDialog()
        }
    }

    private fun showUserProfileDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val dialogBinding = DialogUserProfileBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)

        var selectedAvatarIndex = 0

        // Pre-cargar datos actuales
        lifecycleScope.launch {
            val currentName = themeManager.usernameFlow.first() ?: getString(R.string.default_username)
            dialogBinding.editUsername.setText(currentName)
            selectedAvatarIndex = themeManager.avatarFlow.first()
            highlightAvatar(dialogBinding, selectedAvatarIndex)

            // Precargar tema actual y marcarlo visualmente
            val currentTheme = themeManager.themeFlow.first().type
            highlightTheme(dialogBinding, currentTheme)
        }

        // Listeners de avatares
        dialogBinding.avatar0.setOnClickListener { selectedAvatarIndex = 0; highlightAvatar(dialogBinding, 0) }
        dialogBinding.avatar1.setOnClickListener { selectedAvatarIndex = 1; highlightAvatar(dialogBinding, 1) }
        dialogBinding.avatar2.setOnClickListener { selectedAvatarIndex = 2; highlightAvatar(dialogBinding, 2) }
        dialogBinding.avatar3.setOnClickListener { selectedAvatarIndex = 3; highlightAvatar(dialogBinding, 3) }

        // Listeners de temas con feedback visual
        dialogBinding.btnPandora.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.PANDORA)
            updateTheme(ThemeType.PANDORA)
        }
        dialogBinding.btnHyperion.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.HYPERION)
            updateTheme(ThemeType.HYPERION)
        }
        dialogBinding.btnMaliwan.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.MALIWAN)
            updateTheme(ThemeType.MALIWAN)
        }
        dialogBinding.btnJakobs.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.JAKOBS)
            updateTheme(ThemeType.JAKOBS)
        }
        dialogBinding.btnGuac.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.GUAC)
            updateTheme(ThemeType.GUAC)
        }

        dialogBinding.btnSaveProfile.setOnClickListener {
            val newName = dialogBinding.editUsername.text.toString()
            lifecycleScope.launch {
                if (newName.isNotBlank()) themeManager.saveUsername(newName)
                themeManager.saveAvatar(selectedAvatarIndex)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun highlightAvatar(dialogBinding: DialogUserProfileBinding, index: Int) {
        dialogBinding.avatar0.alpha = if (index == 0) 1.0f else 0.4f
        dialogBinding.avatar1.alpha = if (index == 1) 1.0f else 0.4f
        dialogBinding.avatar2.alpha = if (index == 2) 1.0f else 0.4f
        dialogBinding.avatar3.alpha = if (index == 3) 1.0f else 0.4f

        dialogBinding.avatar0.setPadding(if (index == 0) 0 else 8, if (index == 0) 0 else 8, if (index == 0) 0 else 8, if (index == 0) 0 else 8)
        dialogBinding.avatar1.setPadding(if (index == 1) 0 else 8, if (index == 1) 0 else 8, if (index == 1) 0 else 8, if (index == 1) 0 else 8)
        dialogBinding.avatar2.setPadding(if (index == 2) 0 else 8, if (index == 2) 0 else 8, if (index == 2) 0 else 8, if (index == 2) 0 else 8)
        dialogBinding.avatar3.setPadding(if (index == 3) 0 else 8, if (index == 3) 0 else 8, if (index == 3) 0 else 8, if (index == 3) 0 else 8)
    }

    private fun highlightTheme(dialogBinding: DialogUserProfileBinding, selected: ThemeType) {
        dialogBinding.btnPandora.alpha = if (selected == ThemeType.PANDORA) 1.0f else 0.4f
        dialogBinding.btnHyperion.alpha = if (selected == ThemeType.HYPERION) 1.0f else 0.4f
        dialogBinding.btnMaliwan.alpha = if (selected == ThemeType.MALIWAN) 1.0f else 0.4f
        dialogBinding.btnJakobs.alpha = if (selected == ThemeType.JAKOBS) 1.0f else 0.4f
        dialogBinding.btnGuac.alpha = if (selected == ThemeType.GUAC) 1.0f else 0.4f
    }

    private fun updateTheme(type: ThemeType) {
        lifecycleScope.launch {
            themeManager.saveTheme(type)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MainUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MainUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        cocktailAdapter.submitList(state.cocktails)
                    }
                    is MainUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        val errorMsg = when(state.message) {
                            "Error al cargar categorías ECHOnet" -> getString(R.string.error_categories)
                            "Error de conexión con ECHOnet" -> getString(R.string.error_connection)
                            else -> state.message
                        }
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            themeManager.themeFlow.collectLatest { theme ->
                applyTheme(theme)
            }
        }
    }

    private fun applyTheme(theme: AppTheme) {
        binding.rootLayout.setBackgroundColor(theme.backgroundColor)
        binding.signalIndicator.setTextColor(theme.accentColor)
        binding.userNameTextView.setTextColor(theme.accentColor)
        binding.userAvatarIcon.imageTintList = ColorStateList.valueOf(theme.accentColor)
        binding.searchEditText.setTextColor(theme.textColor)
        binding.searchEditText.setHintTextColor(theme.textColor.withAlpha(128))
        binding.searchEditText.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
        binding.echoFrame.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
    }

    private fun Int.withAlpha(alpha: Int): Int {
        return (this and 0x00FFFFFF) or (alpha shl 24)
    }

    private fun rotateTheme() {
        lifecycleScope.launch {
            val currentTheme = themeManager.themeFlow.first()
            val themes = ThemeType.values()
            val nextIndex = (currentTheme.type.ordinal + 1) % themes.size
            themeManager.saveTheme(themes[nextIndex])
        }
    }
}