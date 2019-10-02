package ru.smityukh.joom2.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface GifRecyclerViewAppearance {
    fun applay(recyclerView: RecyclerView)
    fun applay(gifView: MainFragment.GifView, parent: ViewGroup)
}