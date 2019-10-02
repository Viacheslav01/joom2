package ru.smityukh.joom2.network

import dagger.Module
import dagger.Provides
import ru.smityukh.joom2.infra.ApplicationScope

@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    fun providesHttpClientFactory(): HttpClientFactory = HttpClientFactoryProd()
}