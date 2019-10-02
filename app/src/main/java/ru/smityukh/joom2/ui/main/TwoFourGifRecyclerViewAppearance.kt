package ru.smityukh.joom2.ui.main

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smityukh.joom2.R

/**
 * Из за работы с фреймвоком класс не тестируем без роболектрика.
 */
class TwoFourGifRecyclerViewAppearance() : GifRecyclerViewAppearance {

    private val paramsStorage = arrayOfNulls<AppearanceParams>(2)

    override fun applay(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val params = getParams(context)

        recyclerView.layoutManager = GridLayoutManager(context, params.spanCount, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(params.itemDecorator)
    }

    override fun applay(gifView: MainFragment.GifView, parent: ViewGroup) {
        val itemWidth = getItemWidth(parent)

        gifView.minimumWidth = itemWidth
        gifView.minimumHeight = itemWidth
    }

    private fun getItemWidth(parentView: View): Int {
        val params = getParams(parentView.context)
        return parentView.width / params.spanCount - params.spaceSize
    }

    private fun getParams(context: Context): AppearanceParams {
        val orientation = if (context.resources.configuration.orientation == 2) 1 else 0

        var params = paramsStorage[orientation]
        if (params == null) {
            val spanCount = context.resources.getInteger(R.integer.gifs_list_span_count)
            val spaceSize = context.resources.getDimensionPixelSize(R.dimen.gifs_list_separator_size)
            val spaceHalfSize = spaceSize / 2

            params = AppearanceParams(spanCount, spaceSize, spaceHalfSize)
            paramsStorage[orientation] = params
        }

        return params
    }

    private class AppearanceParams(val spanCount: Int, val spaceSize: Int, val spaceHalfSize: Int) {

        val itemDecorator = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition

                val left = spaceHalfSize
                val right = spaceHalfSize
                val top = if (position < spanCount) spaceSize else 0
                val bottom = spaceSize

                outRect.set(left, top, right, bottom)
            }
        }
    }
}