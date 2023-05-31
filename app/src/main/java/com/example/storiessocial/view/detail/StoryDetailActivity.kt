package com.example.storiessocial.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.storiessocial.databinding.ActivityStoryDetailBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var username : String
    private lateinit var photo : String
    private lateinit var desc : String
    private lateinit var date : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val bundle = intent.extras

        if (bundle != null) {
            username = bundle.getString(USERNAME).toString()
            photo = bundle.getString(PHOTO).toString()
            desc = bundle.getString(DESC).toString()
            date = bundle.getString(DATE).toString()

            Glide.with(this).load(photo).centerCrop().into(binding.photos)
            binding.name.text = username
            binding.desc.text = desc

            val inputFormatStr = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val outputFormatStr = "dd-MM-yyyy HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputFormatStr, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputFormatStr, Locale.getDefault())

            try {
                val dateNew = inputFormat.parse(date)
                val outputDateStr = dateNew?.let { outputFormat.format(it) }
                binding.date.text = outputDateStr
                Log.e("TAG", "Output date: $outputDateStr")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        playAnimation()

    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.name, View.ALPHA, 1f).setDuration(500)
        val date = ObjectAnimator.ofFloat(binding.date, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.desc, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title,date,desc)
            start()
        }
    }

    companion object {
        const val USERNAME = "user_name"
        const val PHOTO = "avatar"
        const val DESC = "DESC"
        const val DATE = "DATE"
    }

}