package com.maruchan.notes.ui.home.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.openActivity
import com.maruchan.notes.R
import com.maruchan.notes.base.BaseActivity
import com.maruchan.notes.databinding.ActivityHomeBinding
import com.maruchan.notes.ui.category.fragment.CategoryFragment
import com.maruchan.notes.ui.favourite.FavoriteFragment
import com.maruchan.notes.ui.home.fragment.HomeFragment
import com.maruchan.notes.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity :  BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {
    private val homeFragment = HomeFragment()
    private val categoryFragment = CategoryFragment()
    private val favoriteFragment = FavoriteFragment()
    private var isUser: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()

        binding.bottomNavigationView.setBackground(null)

        binding.lineProfile.setOnClickListener {
            openActivity<ProfileActivity> ()
        }


        replaceFragment(homeFragment)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeMenu -> {
                    replaceFragment(homeFragment)
                }
                R.id.categoryMenu -> {
                    replaceFragment(categoryFragment)
                }
                R.id.favouriteMenu -> {
                    replaceFragment(favoriteFragment)
                }
            }
            true
        }
    }
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { data ->
                        binding.user = data
                        if (isUser) {
                            getProfile()
                            isUser = false


                        }
                    }
                }
            }

        }
    }
    private fun getProfile(){
        viewModel.getProfile()
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.framcontainer, fragment)
            commit()
        }
    }
}