package ru.smityukh.joom2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import ru.smityukh.joom2.infra.Components
import ru.smityukh.joom2.navigation.DeeplinkHandler
import ru.smityukh.joom2.navigation.Navigation
import ru.smityukh.joom2.navigation.NavigationDispatcher
import ru.smityukh.joom2.navigation.NavigationHost
import ru.smityukh.joom2.ui.detailed.DetailedFragment
import ru.smityukh.joom2.ui.main.MainFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher
    @Inject
    lateinit var navigation: Navigation
    @Inject
    lateinit var deeplinkHandler: DeeplinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Components.Current.getMainComponent().inject(this)

        navigationDispatcher.addNavigationHost(this, NavigationHostImpl())

        if (savedInstanceState == null) {
            val handled = intent.data?.let { deeplinkHandler.handle(it) } ?: false

            if (!handled) {
                navigation.navigateToTrendingList()
            }
        }
    }

    private inner class NavigationHostImpl : NavigationHost {
        override fun navigateToTrendingList(): Boolean {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .addToBackstackIfHasFragment(R.id.container)
                .commit()

            return true
        }

        override fun navigateToDetails(gifId: String): Boolean {
            val transaction = supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, DetailedFragment.newInstance(gifId))
                .addToBackstackIfHasFragment(R.id.container)
                .commit()

            return true
        }

        override fun openUrl(url: String): Boolean {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)

            return true
        }

        private fun FragmentTransaction.addToBackstackIfHasFragment(@IdRes containerViewId: Int): FragmentTransaction {
            if (supportFragmentManager.findFragmentById(containerViewId) != null) {
                addToBackStack(null)
            }

            return this
        }
    }
}
