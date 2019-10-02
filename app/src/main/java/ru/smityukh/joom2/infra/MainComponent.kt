package ru.smityukh.joom2.infra

import dagger.Component
import ru.smityukh.joom2.MainActivity
import ru.smityukh.joom2.navigation.NavigationModule
import ru.smityukh.joom2.network.NetworkModule
import ru.smityukh.joom2.ui.detailed.DetailedFragment
import ru.smityukh.joom2.ui.main.MainFragment
import ru.smityukh.joom2.ui.viewmodel.ViewModelModule

@Component(
    modules = [
        MainModule::class,
        GiphyModule::class,
        ViewModelModule::class,
        NetworkModule::class,
        CommonModule::class,
        NavigationModule::class]
)
@ApplicationScope
interface MainComponent {
    fun inject(fragment: MainFragment)
    fun inject(fragment: DetailedFragment)
    fun inject(activity: MainActivity)
}