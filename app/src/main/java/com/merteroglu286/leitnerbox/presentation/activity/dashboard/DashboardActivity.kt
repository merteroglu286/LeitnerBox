package com.merteroglu286.leitnerbox.presentation.activity.dashboard

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.merteroglu286.leitnerbox.R
import com.merteroglu286.leitnerbox.databinding.ActivityDashboardBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding, DashboardVM>() {

    override fun getViewBinding() = ActivityDashboardBinding.inflate(layoutInflater)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()

    }


    private fun setupBottomNavigation() {

        val navView: BottomNavigationView = binding.bottomNavigation
        navView.apply {
            val navHostFragmet = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard) as NavHostFragment
            val navController = navHostFragmet.navController
            setupWithNavController(navController)

        }}
}