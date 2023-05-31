package com.example.storiessocial.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storiessocial.R
import com.example.storiessocial.ViewModelFactory
import com.example.storiessocial.databinding.ActivityMainBinding
import com.example.storiessocial.model.remote.retrofit.APIConfig
import com.example.storiessocial.view.adapter.LoadingStateAdapter
import com.example.storiessocial.view.adapter.StoriesPagingAdapter
import com.example.storiessocial.view.addStory.AddStoryActivity
import com.example.storiessocial.view.login.LoginActivity
import com.example.storiessocial.view.maps.MapsActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var token: String
    private val adapter = StoriesPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        mainViewModel.getToken().observe(this) {user ->
            if (user != null) {
                if(user.isLogin){
                    token = user.token
                    APIConfig.TOKEN = token
                    getAllStoriesWithPaging()
                }else{
                    token = ""
                }
            }
        }

        binding.addStoryBtn.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
    }

    private fun getAllStoriesWithPaging() {

        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    showLoading(true)
                }
                is LoadState.Error -> {
                    showLoading(false)
                    val msg: String = getString(R.string.errorLoad)
                    Toast.makeText(
                        this@MainActivity,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    showLoading(false)
                }
            }
        }
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.getAllStoriesWithPaging(token).observe(this) {
            adapter.submitData(lifecycle, it)

            if(adapter.snapshot().size == 0){
                showLoading(false)
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
                finish()
                return true
            }
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.maps -> {
                val intent =  Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> true
        }
    }

    private fun setupViewModel(){

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                val intent =  Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.add_story_btn -> {
                val moveIntent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(moveIntent)
            }
        }
    }
}