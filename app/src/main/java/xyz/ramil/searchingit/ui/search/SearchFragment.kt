package xyz.ramil.searchingit.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.ramil.searchingit.R
import xyz.ramil.searchingit.data.model.User
import xyz.ramil.searchingit.ui.main.MainActivity
import xyz.ramil.searchingit.utils.CircularTransformation

class SearchFragment : Fragment() {

    private lateinit var searchFragmentViewModel: SearchFragmentViewModel

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        searchFragmentViewModel =
                ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        val searchAdapter = SearchAdapter { note -> showFullAvatar(note) }

        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.adapter = searchAdapter

        searchFragmentViewModel.items.observe(viewLifecycleOwner, {
            it.let {
                searchAdapter.notifyDataSetChanged()
                searchAdapter.submitList(it)
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    (activity as MainActivity).pagination()
                }
            }
        })


        return root
    }


    fun showFullAvatar(user: User) {

        val ivAvatar = ImageView(context)

        Picasso.get().load(user.avatarUrl)
                .transform(CircularTransformation())
                .into(ivAvatar)

        val frameLayout = context?.let { FrameLayout(it) }

        frameLayout?.addView(ivAvatar)

        frameLayout?.requestFocus()

        frameLayout?.setPadding(resources.getDimensionPixelSize(R.dimen.padding), 0, resources.getDimensionPixelSize(R.dimen.padding), 0)


        val alertDialogBuilder = context?.let { AlertDialog.Builder(it) }

        alertDialogBuilder?.setTitle(user.login)

                ?.setPositiveButton(android.R.string.ok
                ) { dialog, id ->
                    dialog.dismiss() }

        val alert = alertDialogBuilder?.create()
        alert?. requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert?.setView(frameLayout)
        alert?.show()
    }
}