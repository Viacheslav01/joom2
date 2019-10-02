package ru.smityukh.joom2.ui.viewmodel

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

interface JoomViewModelProviders {
    @MainThread
    fun of(fragment: Fragment): ViewModelProvider

    @MainThread
    fun of(activity: FragmentActivity): ViewModelProvider
}