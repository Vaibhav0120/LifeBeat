package com.example.lifebeat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifebeat.Domain.CategoryModel
import com.example.lifebeat.databinding.ViewholderCategoryBinding

class CategoryAdapter(val items:MutableList<CategoryModel>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        context=parent.context
        val binding=ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val item=items[position]
        holder.binding.titleTxt.text=item.Name
        holder.binding.img.setImageResource(context.resources.getIdentifier(item.Picture,"drawable",context.packageName))

        Glide.with(context).load(item.Picture).into(holder.binding.img)
    }

    override fun getItemCount(): Int =items.size

}