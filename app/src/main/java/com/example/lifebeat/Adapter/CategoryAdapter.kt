package com.example.lifebeat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifebeat.Domain.CategoryModel
import com.example.lifebeat.databinding.ViewholderCategoryBinding

class CategoryAdapter(
    val items: MutableList<CategoryModel>,
    private val onCategoryClick: (CategoryModel) -> Unit // Function to handle category clicks
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryModel) {
            binding.titleTxt.text = item.Name
            Glide.with(context).load(item.Picture).into(binding.img)

            // Handle click event
            binding.root.setOnClickListener {
                onCategoryClick(item) // Pass the clicked category back to HomeFragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}
