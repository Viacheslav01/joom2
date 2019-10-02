package ru.smityukh.joom2.ui.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

class JoomViewModelProvidersImpl(private var viewModelFactory: ViewModelProvider.Factory) :
    JoomViewModelProviders {
    override fun of(fragment: Fragment): ViewModelProvider =
        ViewModelProviders.of(fragment, viewModelFactory)

    override fun of(activity: FragmentActivity): ViewModelProvider =
        ViewModelProviders.of(activity, viewModelFactory)
}