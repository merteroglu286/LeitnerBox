package com.merteroglu286.leitnerbox.presentation.activity.auth

import com.merteroglu286.leitnerbox.databinding.ActivityAuthBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding, AuthVM>()  {
    override fun getViewBinding() = ActivityAuthBinding.inflate(layoutInflater)
}