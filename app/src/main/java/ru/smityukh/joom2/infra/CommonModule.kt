package ru.smityukh.joom2.infra

import dagger.Module
import dagger.Provides

@Module
class CommonModule {

    @Provides
    @ApplicationScope
    fun providesExecutors(): Executors = ExecutorsProd()
}