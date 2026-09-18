package com.example.myapy

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CocktailViewModel by viewModels()
    private lateinit var themeManager: ThemeManager
    private lateinit var cocktailAdapter: CocktailAdapter

    private val speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val spokenText = results[0]
                binding.searchEditText.setText(spokenText)
            }
        }
    }

    private val avatars = intArrayOf(
        R.drawable.ic_claptrap_standard,
        R.drawable.ic_claptrap_hyperion,
        R.drawable.ic_claptrap_maliwan,
        R.drawable.ic_claptrap_stealth
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

        binding.voiceSearchButton.setOnClickListener {
            startVoiceSearch()
        }

        // Al pulsar el indicador de señal, rotamos el tema
        binding.signalIndicator.setOnClickListener {
            rotateTheme()
        }

        binding.userProfileSection.setOnClickListener {
            showUserProfileDialog()
        }
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            Toast.makeText(this, getString(R.string.voice_not_supported), Toast.LENGTH_SHORT).show()
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
        dialogBinding.btnTorgue.setOnClickListener {
            highlightTheme(dialogBinding, ThemeType.TORGUE)
            updateTheme(ThemeType.TORGUE)
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
        dialogBinding.btnTorgue.alpha = if (selected == ThemeType.TORGUE) 1.0f else 0.4f
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
                        binding.emptyStateTextView.visibility = if (state.cocktails.isEmpty()) View.VISIBLE else View.GONE
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
                cocktailAdapter.updateTheme(theme)
                startScanLineAnimation()
            }
        }
    }

    private fun startScanLineAnimation() {
        binding.echoScanLine.animate().cancel()
        binding.echoScanLine.translationY = -200f
        binding.echoScanLine.animate()
            .translationY(binding.rootLayout.height.toFloat() + 200f)
            .setDuration(4000)
            .withEndAction { if (!isFinishing) startScanLineAnimation() }
            .start()
    }

    private fun applyTheme(theme: AppTheme) {
        binding.rootLayout.setBackgroundColor(theme.backgroundColor)
        binding.signalIndicator.setTextColor(theme.accentColor)
        binding.userNameTextView.setTextColor(theme.accentColor)
        binding.emptyStateTextView.setTextColor(theme.accentColor)
        // Eliminamos el tintado del icono para ver los colores reales de Claptrap
        binding.userAvatarIcon.imageTintList = null 
        binding.searchEditText.setTextColor(theme.textColor)
        binding.searchEditText.setHintTextColor(theme.textColor.withAlpha(128))
        binding.searchEditText.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
        binding.voiceSearchButton.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
        binding.voiceSearchButton.imageTintList = ColorStateList.valueOf(theme.textColor)
        binding.echoFrame.backgroundTintList = ColorStateList.valueOf(theme.accentColor)
    }

    private fun Int.withAlpha(alpha: Int): Int {
        return (this and 0x00FFFFFF) or (alpha shl 24)
    }

    private fun rotateTheme() {
        lifecycleScope.launch {
            val currentTheme = themeManager.themeFlow.first()
            val themes = ThemeType.entries
            val nextIndex = (currentTheme.type.ordinal + 1) % themes.size
            themeManager.saveTheme(themes[nextIndex])
        }
    }
}