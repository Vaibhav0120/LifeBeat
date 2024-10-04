package com.example.lifebeat.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lifebeat.Domain.DoctorsModel
import com.example.lifebeat.databinding.ViewholderDocListBinding
import com.example.lifebeat.databinding.ViewholderTopDoctorBinding
import com.example.lifebeat.ui.DetailActivity

class DocListAdapter(val items:MutableList<DoctorsModel>): RecyclerView.Adapter<DocListAdapter.ViewHolder>() {
    private var context: Context?=null

    class ViewHolder(val binding: ViewholderDocListBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocListAdapter.ViewHolder {
        context=parent.context
        val binding=ViewholderDocListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocListAdapter.ViewHolder, position: Int) {
        holder.binding.nameTxt.text=items[position].Name
        holder.binding.specialTxt.text=items[position].Special
        holder.binding.scoreTxt.text=items[position].Rating.toString()
        holder.binding.ratingBar.rating=items[position].Rating.toFloat()

        Glide.with(holder.itemView.context).load(items[position].Picture).apply(RequestOptions.circleCropTransform()).into(holder.binding.img)

        holder.binding.makeBtn.setOnClickListener {
            val intent= Intent(context, DetailActivity::class.java)
            intent.putExtra("Object",items[position])
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int =items.size
}