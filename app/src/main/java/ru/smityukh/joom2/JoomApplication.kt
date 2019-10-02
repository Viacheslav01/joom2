package ru.smityukh.joom2

import android.app.Application
import ru.smityukh.joom2.infra.Components
import ru.smityukh.joom2.infra.ComponentsProd

class JoomApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Components.Current.init(ComponentsProd())
    }
}