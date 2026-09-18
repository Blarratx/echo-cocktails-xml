package com.example.myapy.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapy.R
import com.example.myapy.model.Cocktail
import com.example.myapy.ui.theme.AppTheme
import com.example.myapy.utils.DataTranslator

class CocktailAdapter(private val onClick: (Cocktail) -> Unit) : 
    ListAdapter<Cocktail, CocktailAdapter.ViewHolder>(CocktailDiffCallback()) {

    private var currentTheme: AppTheme? = null

    fun updateTheme(theme: AppTheme) {
        currentTheme = theme
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cocktail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cocktail = getItem(position)
        holder.bind(cocktail, onClick, currentTheme)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val container = view.findViewById<View>(R.id.itemContainer)
        private val image = view.findViewById<ImageView>(R.id.cocktailImage)
        private val name = view.findViewById<TextView>(R.id.cocktailName)
        private val category = view.findViewById<TextView>(R.id.cocktailCategory)
        private val shadow = view.findViewById<View>(R.id.itemShadow)

        fun bind(cocktail: Cocktail, onClick: (Cocktail) -> Unit, theme: AppTheme?) {
            name.text = cocktail.name
            
            // Traducir categoría para la lista
            category.text = DataTranslator.translate(
                cocktail.category, 
                DataTranslator.TranslationType.CATEGORY
            )
            
            theme?.let {
                container.backgroundTintList = ColorStateList.valueOf(it.cardColor)
                name.setTextColor(it.accentColor)
                category.setTextColor(it.textColor)
                shadow.backgroundTintList = ColorStateList.valueOf(it.secondaryColor)
            }

            Glide.with(itemView.context)
                .load(cocktail.imageUrl)
                .centerCrop()
                .into(image)

            itemView.setOnClickListener { onClick(cocktail) }
        }
    }
}

class CocktailDiffCallback : DiffUtil.ItemCallback<Cocktail>() {
    override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail) = oldItem == newItem
}
