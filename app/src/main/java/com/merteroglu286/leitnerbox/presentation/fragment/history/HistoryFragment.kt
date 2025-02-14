package com.merteroglu286.leitnerbox.presentation.fragment.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.merteroglu286.leitnerbox.databinding.FragmentHistoryBinding
import com.merteroglu286.leitnerbox.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryVM>() {


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()
    }

    override fun setListeners() {
        super.setListeners()


    }
}