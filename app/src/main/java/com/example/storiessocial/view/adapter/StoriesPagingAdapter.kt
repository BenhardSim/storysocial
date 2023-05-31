package com.example.storiessocial.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storiessocial.databinding.ItemStoriesBinding
import com.example.storiessocial.model.local.entity.StoryItem
import com.example.storiessocial.view.detail.StoryDetailActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StoriesPagingAdapter: PagingDataAdapter<StoryItem, StoriesPagingAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryItem) {
            binding.tvItemName.text = data.name
            Glide.with(binding.root.context).load(data.photoUrl).centerCrop().into(binding.imgItemPhoto)

            val inputFormatStr = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputFormatStr = "dd-MM-yyyy HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputFormatStr, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputFormatStr, Locale.getDefault())

            try {
                val date = data.createdAt?.let { inputFormat.parse(it) }
                val outputDateStr = date?.let { outputFormat.format(it) }
                binding.date.text = outputDateStr
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            binding.root.setOnClickListener{
                val moveIntentUsers = Intent(binding.root.context,StoryDetailActivity::class.java)
                moveIntentUsers.putExtra(StoryDetailActivity.USERNAME, data.name)
                moveIntentUsers.putExtra(StoryDetailActivity.PHOTO, data.photoUrl)
                moveIntentUsers.putExtra(StoryDetailActivity.DESC, data.description)
                moveIntentUsers.putExtra(StoryDetailActivity.DATE, data.createdAt)
                binding.root.context.startActivity(moveIntentUsers)
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}