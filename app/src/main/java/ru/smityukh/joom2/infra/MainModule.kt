package ru.smityukh.joom2.infra

import dagger.Module
import dagger.Provides
import ru.smityukh.joom2.ui.main.GifRecyclerViewAppearance
import ru.smityukh.joom2.ui.main.TwoFourGifRecyclerViewAppearance

@Module
class MainModule {

    @Provides
    @ApplicationScope
    fun providesGifRecyclerViewAppearance(): GifRecyclerViewAppearance = TwoFourGifRecyclerViewAppearance()
}