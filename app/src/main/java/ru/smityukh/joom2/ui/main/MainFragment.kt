package ru.smityukh.joom2.ui.main

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.smityukh.joom2.R
import ru.smityukh.joom2.data.GifInfo
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.ifNotNull
import ru.smityukh.joom2.ifNull
import ru.smityukh.joom2.infra.Components
import ru.smityukh.joom2.ui.viewmodel.JoomViewModelProviders
import javax.inject.Inject

class MainFragment : Fragment() {

    companion object {
        private const val TAG = "MainFragment"

        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeToRefresh: SwipeRefreshLayout

    @Inject
    lateinit var recyclerViewAppearance: GifRecyclerViewAppearance
    @Inject
    lateinit var viewModelProviders: JoomViewModelProviders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Components.Current.getMainComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.gifs_recycler_view)
        swipeToRefresh = view.findViewById(R.id.swipe_refresh_layout)

        recyclerViewAppearance.applay(recyclerView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = viewModelProviders.of(this).get(MainViewModel::class.java)

        bindUi()
    }

    private fun bindUi() {
        val gifAdapter = GifAdapter(recyclerViewAppearance) { viewModel.selectGif(it) }

        viewModel.trendingGifs.observe(this, Observer {
            Log.d(TAG, "update adapter by new list")
            gifAdapter.submitList(it)
        })
        viewModel.isTrendingGifsLoadeing.observe(this, Observer {
            Log.d(TAG, "update refreshing: $it")
            swipeToRefresh.isRefreshing = it
        })
        viewModel.error.observe(this, object : Observer<GiphyStore.StoreDataError?> {
            private var currentSnackbar: Snackbar? = null

            override fun onChanged(error: GiphyStore.StoreDataError?) {
                currentSnackbar?.dismiss()
                currentSnackbar = null

                if (error == null) {
                    Log.d(TAG, "hide error snackbar")
                    return
                }

                Log.d(TAG, "show error snackbar")

                currentSnackbar = Snackbar.make(recyclerView, error.message, Snackbar.LENGTH_INDEFINITE).apply {
                    if (error.action != null && error.actionName.isNullOrBlank()) {
                        setAction(error.actionName) { error.action?.invoke() }
                    }
                    show()
                }
            }
        })

        recyclerView.adapter = gifAdapter

        swipeToRefresh.setOnRefreshListener { viewModel.reloadTrendingGifs() }
    }

    class GifAdapter(private val appearance: GifRecyclerViewAppearance, private val itemClickHandler: (GifInfo) -> Unit) :
        PagedListAdapter<GifInfo, GifViewHolder>(DIFF) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
            val view = GifView(parent.context)
            appearance.applay(view, parent)

            return GifViewHolder(view, itemClickHandler)
        }

        override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
            getItem(position)
                .ifNotNull { holder.bind(it) }
                .ifNull { holder.clean() }
        }

        companion object {
            val DIFF = object : DiffUtil.ItemCallback<GifInfo>() {
                override fun areItemsTheSame(oldItem: GifInfo, newItem: GifInfo): Boolean = oldItem.key == newItem.key
                override fun areContentsTheSame(oldItem: GifInfo, newItem: GifInfo): Boolean = oldItem.key == newItem.key
            }
        }
    }

    class GifViewHolder(private val view: GifView, private val itemClickHandler: (GifInfo) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(gifInfo: GifInfo) {
            Log.d(TAG, "GifViewHolder.bind(): gifInfo=[$gifInfo]")

            // TODO: глайд не позволяет использовать тэг на картинке, поэтому не просто сделать общий обработчик
            // кликов и приходится использовать зымыкание для сохранения ссылки на объект картинки
            // если сделать вью элемента не из одной картинки, а добавить к нему контейнер, то можно будет заменить эту рализацию
            view.setOnClickListener { itemClickHandler(gifInfo) }

            val glide = Glide.with(view)
            glide
                .load(gifInfo.imageUrl)
                .error(R.drawable.ic_error_outline_black_24dp)
                .placeholder(R.drawable.ic_file_download_black_24dp)
                .thumbnail(glide.load(gifInfo.stillImageUrl).centerCrop())
                .centerCrop()
                .into(view)
        }

        fun clean() {
            Log.d(TAG, "GifViewHolder.clean()")

            view.setOnClickListener(null)
            Glide.with(view).clear(view)
        }
    }

    class GifView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr)
}