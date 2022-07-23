package com.bangkit.mystoryapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.mystoryapp.R
import com.bangkit.mystoryapp.ViewModelFactory
import com.bangkit.mystoryapp.databinding.ActivityMainBinding
import com.bangkit.mystoryapp.network.response.StoryResponse
import com.bangkit.mystoryapp.utility.UserPreference
import com.bangkit.mystoryapp.view.login.LoginActivity
import com.bangkit.mystoryapp.view.profile.ProfileUserActivity
import kotlinx.coroutines.Job
import java.util.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var main: Job = Job()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupViewModel()
        getToken()
        setupAdapter()
    }

    private fun setupAction() {
        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryUploadActivity::class.java)
            intent.putExtra(EXTRA_TOKEN, token)
            startActivity(intent)
        }
    }


    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.isLoading.observe(this@MainActivity) {
            isLoading(it)
        }
    }

    private fun getToken() {
        mainViewModel.getUserToken().observe(this@MainActivity) {
            if (it != null) {
                token = it
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAdapter(){
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        binding.apply {
            rvStory.layoutManager = layoutManager
            rvStory.addItemDecoration(itemDecoration)
        }
    }

    private fun setStoryData(listStory: List<StoryResponse.Story>){
        val list = ArrayList<StoryResponse.Story>()
        for(storyId in listStory){
            val story = StoryResponse.Story(
                storyId.id,
                storyId.name,
                storyId.description,
                storyId.photoUrl,
                storyId.createdAt
            )
            list.add(story)
        }
        val storyAdapter = ListStoryAdapter(list)
        binding.rvStory.adapter = storyAdapter
    }

    private fun isLoading(loading: Boolean){
        binding.apply {
            if(loading){
                rvStory.visibility = View.INVISIBLE
                shimmerLoading.visibility = View.VISIBLE
                shimmerLoading.startShimmer()
                pbLoading.visibility = View.VISIBLE
            }else{
                rvStory.visibility = View.VISIBLE
                shimmerLoading.visibility = View.INVISIBLE
                shimmerLoading.startShimmer()
                pbLoading.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.profile ->{
                val intent = Intent(this@MainActivity, ProfileUserActivity::class.java)
                startActivity(intent)
                true
            }else -> false
        }
    }
}