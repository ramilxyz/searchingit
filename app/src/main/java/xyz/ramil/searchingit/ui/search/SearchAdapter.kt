package xyz.ramil.searchingit.ui.search

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.ramil.searchingit.R
import xyz.ramil.searchingit.data.model.User
import xyz.ramil.searchingit.utils.CircularTransformation

class SearchAdapter (private val onClick: (User) -> Unit) :
    ListAdapter<User, SearchAdapter.ItemViewHolder>(ItemDiffCallback) {

    class ItemViewHolder(itemView: View, val onClick: (User) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)

        private var user1: User? = null
        private var oldItem: User? = null

        init {
            itemView.setOnClickListener {
                user1?.let {
                    onClick(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            user1 = user
            tvName.text = user.login
            tvDescription.text = user.type

            Picasso.get().load(user.avatarUrl)
                .transform(CircularTransformation())
                .into(ivImage)

            oldItem = user1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)

    }
}

object ItemDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }
}
