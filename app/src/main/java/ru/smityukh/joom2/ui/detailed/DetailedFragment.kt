package ru.smityukh.joom2.ui.detailed

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE

import ru.smityukh.joom2.R
import ru.smityukh.joom2.data.GifInfo
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.infra.Components
import ru.smityukh.joom2.ui.viewmodel.JoomViewModelProviders
import javax.inject.Inject

class DetailedFragment : Fragment() {

    companion object {
        private const val PARAM_GIF_ID = "GIF-ID"

        fun newInstance(gifId: String) = DetailedFragment().also {
            val params = Bundle()
            params.putString(PARAM_GIF_ID, gifId)

            it.arguments = params
        }
    }

    private lateinit var gifImageView: ImageView
    private lateinit var avatarImageView: ImageView
    private lateinit var userNameView: TextView
    private lateinit var userDisplayNameView: TextView
    private lateinit var userProfileView: TextView
    private lateinit var progressRing: View

    @Inject
    lateinit var viewModelProviders: JoomViewModelProviders

    private lateinit var viewModel: DetailedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Components.Current.getMainComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detailed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            gifImageView = view.findViewById(R.id.gif_image)
            avatarImageView = view.findViewById(R.id.avatar_image)
            userNameView = view.findViewById(R.id.user_name)
            userDisplayNameView = view.findViewById(R.id.user_display_name)
            userProfileView = view.findViewById(R.id.user_profile)
            progressRing = view.findViewById(R.id.progress_ring)
        }

        userProfileView.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                viewModel.openProfile()
            }
            isClickable = true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = viewModelProviders.of(this).get(DetailedViewModel::class.java)
        viewModel.setSelectedGifId(getGifIdFromParams())

        bindUi()
    }

    private fun bindUi() {
        viewModel.gif.observe(this, Observer {
            if (it != null) {
                fillUi(it)
            } else {
                cleanUi()
            }
        })

        viewModel.error.observe(this, object : Observer<GiphyStore.StoreDataError?> {
            private var currentSnackbar: Snackbar? = null

            override fun onChanged(error: GiphyStore.StoreDataError?) {
                currentSnackbar?.dismiss()
                currentSnackbar = null

                if (error == null) {
                    return
                }

                currentSnackbar = Snackbar.make(gifImageView, error.message, LENGTH_INDEFINITE).apply {
                    if (error.action != null && error.actionName.isNullOrBlank()) {
                        setAction(error.actionName) { error.action?.invoke() }
                    }
                    show()
                }
            }
        })

        viewModel.isLoading.observe(this, Observer {
            progressRing.visibility = if (it == true) View.VISIBLE else View.GONE
        })
    }

    private fun fillUi(gif: GifInfo) {
        fillGifImageView(gif)

        fillAvatarImageView(gif.user.avatarUrl)

        fillTextView(userNameView, gif.user.name)
        fillTextView(userDisplayNameView, gif.user.displayName)
        fillTextView(userProfileView, gif.user.profileUrl)
    }

    private fun fillTextView(view: TextView, text: String) {
        view.text = text
        view.visibility = if (text.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun fillGifImageView(gif: GifInfo) {
        val glide = Glide.with(this)
        glide
            .load(gif.imageUrl)
            .error(R.drawable.ic_error_outline_black_24dp)
            .placeholder(R.drawable.ic_file_download_black_24dp)
            .thumbnail(glide.load(gif.stillImageUrl).fitCenter())
            .fitCenter()
            .into(gifImageView)
    }

    private fun fillAvatarImageView(url: String) {
        if (url.isBlank()) {
            avatarImageView.visibility = View.GONE
            return
        }

        avatarImageView.visibility = View.VISIBLE

        Glide.with(this)
            .load(url)
            .circleCrop()
            .into(avatarImageView)
    }

    private fun cleanUi() {
        Glide.with(this).run {
            clear(gifImageView)
            clear(avatarImageView)
        }
    }

    private fun getGifIdFromParams(): String = arguments?.getString(PARAM_GIF_ID) ?: ""
}
